package com.willfp.eco.internal.spigot.data

import com.willfp.eco.internal.spigot.data.handlers.impl.ExposedBookkeeping
import com.willfp.eco.internal.spigot.datapack.BookkeepingLedgerStorage
import com.willfp.eco.internal.spigot.datapack.LEDGER_PREFIX
import java.nio.file.Files
import org.jetbrains.exposed.v1.jdbc.Database
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The store that replaced data.yml. Both implementations answer the same questions, so both are
 * held to the same tests: the memory one is what the rest of the suite uses, and the sqlite one is
 * what a server actually runs.
 */
class BookkeepingTests {
    private fun stores(): List<Bookkeeping> {
        val file = Files.createTempDirectory("eco-bookkeeping").resolve("data.db").toFile()

        return listOf(
            MemoryBookkeeping(),
            ExposedBookkeeping(Database.connect("jdbc:sqlite:${file.absolutePath}", "org.sqlite.JDBC"), "eco_")
        )
    }

    private fun eachStore(check: (Bookkeeping) -> Unit) = stores().forEach(check)

    @Test
    fun `a key that was never written reads as absent`() = eachStore {
        assertNull(it.get("previous-handler"))
        assertFalse(it.getBool("legacy-data-migrated"))
        assertTrue(it.getList("backfilled-keys").isEmpty())
        assertFalse(it.has("previous-handler"))
    }

    @Test
    fun `a value round trips`() = eachStore {
        it.set("previous-handler", "sqlite")

        assertEquals("sqlite", it.get("previous-handler"))
        assertTrue(it.has("previous-handler"))
    }

    @Test
    fun `a flag round trips`() = eachStore {
        it.setBool("legacy-data-migrated", true)

        assertTrue(it.getBool("legacy-data-migrated"))
    }

    @Test
    fun `a list round trips in order`() = eachStore {
        it.setList("backfilled-keys", listOf("c:one", "a:two", "b:three"))

        assertEquals(listOf("c:one", "a:two", "b:three"), it.getList("backfilled-keys"))
    }

    @Test
    fun `writing a shorter list drops what it replaced`() = eachStore {
        it.setList("backfilled-keys", listOf("one", "two", "three"))
        it.setList("backfilled-keys", listOf("one"))

        assertEquals(listOf("one"), it.getList("backfilled-keys"))
    }

    @Test
    fun `a null value removes the key`() = eachStore {
        it.set("previous-handler", "sqlite")
        it.set("previous-handler", null)

        assertNull(it.get("previous-handler"))
    }

    @Test
    fun `keys are found by prefix, and only by their own`() = eachStore {
        it.setList("${LEDGER_PREFIX}ecoenchants", listOf("a"))
        it.setList("${LEDGER_PREFIX}ecoitems", listOf("b"))
        it.set("previous-handler", "sqlite")

        assertEquals(
            setOf("${LEDGER_PREFIX}ecoenchants", "${LEDGER_PREFIX}ecoitems"),
            it.keysStartingWith(LEDGER_PREFIX)
        )
    }

    @Test
    fun `a prefix holding a wildcard matches literally`() = eachStore {
        it.set("data_one", "a")
        it.set("dataXone", "b")

        assertEquals(setOf("data_one"), it.keysStartingWith("data_"))
    }

    @Test
    fun `the ledger round trips through the store`() = eachStore {
        val storage = BookkeepingLedgerStorage(it)

        storage.write(mapOf("ecoenchants" to setOf("worldgen/biome|eco:test")))

        assertEquals(mapOf("ecoenchants" to setOf("worldgen/biome|eco:test")), storage.read())
    }

    @Test
    fun `a plugin dropped from the ledger stops being read back`() = eachStore {
        val storage = BookkeepingLedgerStorage(it)

        storage.write(mapOf("ecoenchants" to setOf("a"), "ecoitems" to setOf("b")))
        storage.write(mapOf("ecoitems" to setOf("b")))

        assertEquals(mapOf("ecoitems" to setOf("b")), storage.read())
    }

    @Test
    fun `an empty ledger reads as empty`() = eachStore {
        assertTrue(BookkeepingLedgerStorage(it).read().isEmpty())
    }
}
