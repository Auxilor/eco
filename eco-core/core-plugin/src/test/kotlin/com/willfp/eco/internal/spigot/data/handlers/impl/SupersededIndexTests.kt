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
                statement.executeUpdate(
                    """
                    CREATE TABLE eco_int (
                        profileUUID BINARY(16) NOT NULL,
                        dataKey VARCHAR(128) NOT NULL,
                        dataValue INT NOT NULL,
                        CONSTRAINT pk_eco_int PRIMARY KEY (profileUUID, dataKey)
                    )
                    """.trimIndent()
                )
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

            // The uniqueness it enforced has to outlive it, which is what makes the drop safe.
            assertTrue(
                connection.hasIndex("sqlite_autoindex_eco_int_1"),
                "the primary key's own index should still be there"
            )

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

    private fun Connection.hasIndex(name: String): Boolean =
        prepareStatement("SELECT 1 FROM sqlite_master WHERE type = 'index' AND name = ?").use { statement ->
            statement.setString(1, name)
            statement.executeQuery().use { it.next() }
        }

    private fun openRaw(path: String): Connection {
        Class.forName("org.sqlite.JDBC")
        return DriverManager.getConnection("jdbc:sqlite:$path")
    }
}
