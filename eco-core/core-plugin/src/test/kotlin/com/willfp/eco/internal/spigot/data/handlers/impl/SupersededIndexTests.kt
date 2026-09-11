package com.willfp.eco.internal.spigot.data.handlers.impl

import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * Older versions of eco gave every table a unique index over the same columns as its primary key,
 * which is a second full copy of that index -- on a store of a million rows, a third of the file.
 * The handler drops it on startup, and must do so without touching the rows or the constraint the
 * primary key still enforces.
 */
class SupersededIndexTests {
    @Test
    fun `startup drops the index that duplicates the primary key`(@TempDir dir: Path) {
        val file = dir.resolve("data.db").toFile()

        openRaw(file.absolutePath).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(LEGACY_INT_TABLE)
                statement.executeUpdate(
                    "CREATE UNIQUE INDEX eco_int_profileUUID_dataKey ON eco_int (profileUUID, dataKey)"
                )
                statement.executeUpdate(
                    "INSERT INTO eco_int VALUES (x'0102030405060708090a0b0c0d0e0f10', 'test:key', 42)"
                )
            }
        }

        SQLitePersistentDataHandler(file)

        openRaw(file.absolutePath).use { connection ->
            assertFalse(
                connection.hasIndex("eco_int_profileUUID_dataKey"),
                "the index duplicating the primary key should have been dropped"
            )

            // Once the table is clustered on its primary key there is no separate index left at
            // all -- the table is the index. What has to outlive the drop is the uniqueness, which
            // is asserted below.
            assertEquals(0, connection.indexCount(), "no secondary index should be left on the table")

            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT dataValue FROM eco_int").use { results ->
                    assertTrue(results.next(), "the existing row should have survived")
                    assertEquals(42, results.getInt(1))
                    assertFalse(results.next(), "no extra rows should have appeared")
                }

                val duplicate = runCatching {
                    statement.executeUpdate(
                        "INSERT INTO eco_int VALUES (x'0102030405060708090a0b0c0d0e0f10', 'test:key', 7)"
                    )
                }

                assertTrue(duplicate.isFailure, "a duplicate (profileUUID, dataKey) should still be rejected")
            }
        }
    }


    @Test
    fun `startup rebuilds a rowid table to store its rows in the primary key`(@TempDir dir: Path) {
        val file = dir.resolve("data.db").toFile()

        openRaw(file.absolutePath).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(LEGACY_INT_TABLE)
                statement.executeUpdate(
                    "CREATE UNIQUE INDEX eco_int_profileUUID_dataKey ON eco_int (profileUUID, dataKey)"
                )

                for (i in 1..50) {
                    statement.executeUpdate(
                        "INSERT INTO eco_int VALUES (x'0102030405060708090a0b0c0d0e0f10', 'test:key$i', $i)"
                    )
                }
            }
        }

        SQLitePersistentDataHandler(file)

        openRaw(file.absolutePath).use { connection ->
            assertTrue(
                connection.tableSql("eco_int").contains("WITHOUT ROWID"),
                "the table should have been rebuilt to store its rows in the primary key"
            )

            // The rewrite copies the statement the database already had, so what an older version
            // of eco put in the schema has to come back with it.
            assertTrue(
                connection.tableSql("eco_int").contains("chk_eco_int_signed_integer_dataValue"),
                "the existing CHECK constraint should have survived the rebuild"
            )

            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*), SUM(dataValue) FROM eco_int").use { results ->
                    assertTrue(results.next())
                    assertEquals(50, results.getInt(1), "every row should have survived")
                    assertEquals(1275, results.getInt(2), "every value should have survived")
                }

                // The scratch table the rewrite renames the original to must not be left behind.
                assertFalse(connection.hasTable("eco_int_with_rowid"))

                // Nothing reads a rowid, and a rebuilt table no longer has one to read.
                assertTrue(
                    runCatching { statement.executeQuery("SELECT rowid FROM eco_int") }.isFailure,
                    "a rebuilt table should have no rowid"
                )
            }
        }
    }

    @Test
    fun `a rebuild that cannot complete leaves the original table alone`(@TempDir dir: Path) {
        val file = dir.resolve("data.db").toFile()

        openRaw(file.absolutePath).use { connection ->
            connection.createStatement().use { statement ->
                // A rowid table lets a PRIMARY KEY column hold NULL when it is not also declared
                // NOT NULL, which is a documented SQLite quirk; a WITHOUT ROWID table does not.
                // So the rewrite gets as far as renaming the original away and creating the new
                // table, and only then fails, on the copy -- the point at which a rollback has
                // something to undo.
                statement.executeUpdate(
                    """
                    CREATE TABLE eco_int (
                        profileUUID BINARY(16),
                        dataKey VARCHAR(128),
                        dataValue INT,
                        CONSTRAINT pk_eco_int PRIMARY KEY (profileUUID, dataKey)
                    )
                    """.trimIndent()
                )
                statement.executeUpdate(
                    "INSERT INTO eco_int VALUES (x'0102030405060708090a0b0c0d0e0f10', 'test:key', 42)"
                )
                statement.executeUpdate("INSERT INTO eco_int VALUES (NULL, NULL, 7)")
            }
        }

        runCatching { SQLitePersistentDataHandler(file) }

        openRaw(file.absolutePath).use { connection ->
            assertFalse(
                connection.tableSql("eco_int").contains("WITHOUT ROWID"),
                "the failed rebuild should not have taken effect"
            )
            assertFalse(
                connection.hasTable("eco_int_with_rowid"),
                "the scratch table the rewrite renamed the original to should have been rolled back"
            )

            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT COUNT(*), SUM(dataValue) FROM eco_int").use { results ->
                    assertTrue(results.next(), "the original table should still be in place")
                    assertEquals(2, results.getInt(1), "both original rows should have survived")
                    assertEquals(49, results.getInt(2))
                }
            }
        }
    }

    private fun Connection.tableSql(name: String): String =
        prepareStatement("SELECT sql FROM sqlite_master WHERE type = 'table' AND name = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { if (it.next()) it.getString(1) else "" }
        }

    private fun Connection.hasTable(name: String): Boolean =
        prepareStatement("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { it.next() }
        }

    private fun Connection.hasIndex(name: String): Boolean =
        prepareStatement("SELECT 1 FROM sqlite_master WHERE type = 'index' AND name = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { it.next() }
        }

    private fun Connection.indexCount(): Int =
        createStatement().use { statement ->
            statement.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = 'index'").use {
                if (it.next()) it.getInt(1) else 0
            }
        }

    private fun openRaw(path: String): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection("jdbc:sqlite:$path")
    }
}

private val LEGACY_INT_TABLE = """
    CREATE TABLE eco_int (
        profileUUID BINARY(16) NOT NULL,
        dataKey VARCHAR(128) NOT NULL,
        dataValue INT NOT NULL,
        CONSTRAINT pk_eco_int PRIMARY KEY (profileUUID, dataKey),
        CONSTRAINT chk_eco_int_signed_integer_dataValue CHECK (dataValue BETWEEN -2147483648 AND 2147483647)
    )
""".trimIndent()
