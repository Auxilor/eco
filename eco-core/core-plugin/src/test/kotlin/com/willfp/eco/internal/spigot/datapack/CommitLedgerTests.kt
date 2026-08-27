package com.willfp.eco.internal.spigot.datapack

import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CommitLedgerTests {
    private fun entry(registry: String, key: String) =
        DatapackEntry(registry, NamespacedKey("test", key), "{}".toByteArray(), true)

    @Test
    fun `bootstrap entries are committed`() {
        val ledger = CommitLedger(MemoryLedgerStorage())

        ledger.commit("myplugin", listOf(entry("worldgen/biome", "swamp")))

        Assertions.assertEquals(setOf("worldgen/biome|test:swamp"), ledger.committed("myplugin"))
    }

    @Test
    fun `reloadable entries are never committed`() {
        val ledger = CommitLedger(MemoryLedgerStorage())

        ledger.commit("myplugin", listOf(entry("recipe", "thing"), entry("tags/block", "thing")))

        Assertions.assertTrue(ledger.committed("myplugin").isEmpty())
    }

    @Test
    fun `committing is cumulative and deduplicated`() {
        val ledger = CommitLedger(MemoryLedgerStorage())

        ledger.commit("myplugin", listOf(entry("damage_type", "a")))
        ledger.commit("myplugin", listOf(entry("damage_type", "a"), entry("damage_type", "b")))

        Assertions.assertEquals(
            setOf("damage_type|test:a", "damage_type|test:b"),
            ledger.committed("myplugin")
        )
    }

    @Test
    fun `releasing tokens forgets only those entries`() {
        val ledger = CommitLedger(MemoryLedgerStorage())
        val kept = entry("dimension", "void")
        val dropped = entry("dimension", "old")

        ledger.commit("myplugin", listOf(kept, dropped))
        ledger.releaseTokens("myplugin", listOf(CommitLedger.token(dropped)))

        Assertions.assertEquals(setOf("dimension|test:void"), ledger.committed("myplugin"))
    }

    @Test
    fun `releasing every token clears the plugin`() {
        val ledger = CommitLedger(MemoryLedgerStorage())
        val committed = entry("dimension", "void")

        ledger.commit("myplugin", listOf(committed))
        ledger.releaseTokens("myplugin", listOf(CommitLedger.token(committed)))

        Assertions.assertTrue(ledger.committed("myplugin").isEmpty())
    }

    @Test
    fun `plugins are tracked separately`() {
        val ledger = CommitLedger(MemoryLedgerStorage())

        ledger.commit("a", listOf(entry("dimension", "one")))
        ledger.commit("b", listOf(entry("dimension", "two")))

        Assertions.assertEquals(setOf("dimension|test:one"), ledger.committed("a"))
        Assertions.assertEquals(setOf("dimension|test:two"), ledger.committed("b"))
    }

    @Test
    fun `release forgets a plugin`() {
        val ledger = CommitLedger(MemoryLedgerStorage())

        ledger.commit("myplugin", listOf(entry("dimension", "void")))
        ledger.release("myplugin")

        Assertions.assertTrue(ledger.committed("myplugin").isEmpty())
    }

    @Test
    fun `the token ignores the file extension`() {
        val json = DatapackEntry("worldgen/biome", NamespacedKey("test", "a"), "{}".toByteArray(), true)
        val binary = DatapackEntry("worldgen/biome", NamespacedKey("test", "a"), byteArrayOf(1), false)

        Assertions.assertEquals(CommitLedger.token(json), CommitLedger.token(binary))
    }
}
