package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.internal.spigot.data.MemoryBookkeeping
import com.willfp.eco.internal.spigot.data.getBool
import com.willfp.eco.internal.spigot.data.handlers.configOf
import com.willfp.eco.internal.spigot.data.handlers.stubEcoConfigFactory
import com.willfp.eco.internal.spigot.data.handlers.unstubEcoConfigFactory
import com.willfp.eco.internal.spigot.datapack.LEDGER_PREFIX
import java.io.File
import java.nio.file.Files
import java.util.UUID
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Once the migration is done with data.yml, the file is dead weight: loaded on boot, walked by the
 * config updater on the main thread, and deleted and rewritten whole on every autosave -- which is
 * also why deleting it by hand did not stick. Retirement moves everything eco still needs into the
 * database and takes the file away.
 */
class DataYmlRetirementTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() = stubEcoConfigFactory()

        @JvmStatic
        @AfterAll
        fun unstubEco() = unstubEcoConfigFactory()
    }

    private val alice = UUID.randomUUID()

    private fun dataYml() = configOf(
        "previous-handler" to "sqlite",
        LEGACY_MIGRATED_KEY to true,
        LOCAL_MIGRATED_KEY to true,
        BACKFILLED_KEYS_KEY to listOf("live:first", "live:second"),
        "datapacks" to mapOf(
            "ecoenchants" to listOf("worldgen/biome|eco:test")
        ),
        "player" to mapOf(
            alice.toString() to mapOf("live:first" to "alice first")
        )
    )

    @Test
    fun `retiring records the backup the backfill reads from then on`() {
        val bookkeeping = MemoryBookkeeping()

        retireDataYmlProfiles(bookkeeping, "data.yml.1700000000000.bak")

        assertEquals("data.yml.1700000000000.bak", bookkeeping.get(BACKFILL_SOURCE_KEY))
    }

    @Test
    fun `an empty player section is not profiles`() {
        assertFalse(holdsProfiles(configOf("player" to emptyMap<String, Any>())))
        assertFalse(holdsProfiles(configOf("previous-handler" to "sqlite")))
        assertTrue(holdsProfiles(dataYml()))
    }

    @Test
    fun `importing carries eco's own bookkeeping across`() {
        val bookkeeping = MemoryBookkeeping()

        assertTrue(importBookkeeping(dataYml(), bookkeeping))

        assertEquals("sqlite", bookkeeping.get(PREVIOUS_HANDLER_KEY))
        assertTrue(bookkeeping.getBool(LEGACY_MIGRATED_KEY))
        assertTrue(bookkeeping.getBool(LOCAL_MIGRATED_KEY))
        assertEquals(listOf("live:first", "live:second"), bookkeeping.getList(BACKFILLED_KEYS_KEY))
    }

    @Test
    fun `importing carries the datapack ledger across`() {
        val bookkeeping = MemoryBookkeeping()

        importBookkeeping(dataYml(), bookkeeping)

        assertEquals(
            listOf("worldgen/biome|eco:test"),
            bookkeeping.getList("${LEDGER_PREFIX}ecoenchants")
        )
    }

    @Test
    fun `importing leaves the profiles alone`() {
        val bookkeeping = MemoryBookkeeping()

        importBookkeeping(dataYml(), bookkeeping)

        assertTrue(bookkeeping.keysStartingWith(PLAYER_SECTION).isEmpty())
    }

    @Test
    fun `a stale data yml never overwrites the store`() {
        val bookkeeping = MemoryBookkeeping()
        bookkeeping.set(PREVIOUS_HANDLER_KEY, "mysql")
        bookkeeping.setList(BACKFILLED_KEYS_KEY, listOf("live:third"))

        importBookkeeping(dataYml(), bookkeeping)

        assertEquals("mysql", bookkeeping.get(PREVIOUS_HANDLER_KEY))
        assertEquals(listOf("live:third"), bookkeeping.getList(BACKFILLED_KEYS_KEY))
    }

    @Test
    fun `importing a data yml with nothing in it carries nothing`() {
        assertFalse(importBookkeeping(configOf(), MemoryBookkeeping()))
    }

    @Test
    fun `deleting takes the file away, and says so only when there was one`() {
        val folder = Files.createTempDirectory("eco-retirement").toFile()
        val file = File(folder, "data.yml")

        assertFalse(deleteDataYml(folder))

        file.writeText("previous-handler: sqlite\n")

        assertTrue(deleteDataYml(folder))
        assertFalse(file.exists())
    }
}
