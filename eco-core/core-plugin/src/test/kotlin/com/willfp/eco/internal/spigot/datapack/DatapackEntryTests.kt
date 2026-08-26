package com.willfp.eco.internal.spigot.datapack

import org.bukkit.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DatapackEntryTests {
    private val draft = EcoDatapackDraft()

    @Test
    fun `text entries default to json`() {
        val entry = DatapackEntry("worldgen/biome", NamespacedKey("test", "swamp"), "{}".toByteArray(), true)

        Assertions.assertEquals("data/test/worldgen/biome/swamp.json", entry.path)
        Assertions.assertTrue(entry.isJson)
    }

    @Test
    fun `binary entries default to nbt`() {
        val entry = DatapackEntry("structure", NamespacedKey("test", "hut"), byteArrayOf(1), false)

        Assertions.assertEquals("data/test/structure/hut.nbt", entry.path)
        Assertions.assertFalse(entry.isJson)
    }

    @Test
    fun `functions are mcfunction source, not json`() {
        val entry = DatapackEntry("function", NamespacedKey("test", "tick"), "say hi".toByteArray(), true)

        Assertions.assertEquals("data/test/function/tick.mcfunction", entry.path)
        Assertions.assertFalse(entry.isJson)
    }

    @Test
    fun `an explicit extension on the id is respected`() {
        val entry = DatapackEntry("structure", NamespacedKey("test", "hut.nbt"), byteArrayOf(1), false)

        Assertions.assertEquals("data/test/structure/hut.nbt", entry.path)
    }

    @Test
    fun `ids may be nested`() {
        val entry = DatapackEntry("loot_table", NamespacedKey("test", "chests/simple"), "{}".toByteArray(), true)

        Assertions.assertEquals("data/test/loot_table/chests/simple.json", entry.path)
    }

    @Test
    fun `the draft keeps insertion order`() {
        draft.put("recipe", NamespacedKey("test", "b"), "{}")
        draft.put("recipe", NamespacedKey("test", "a"), "{}")

        Assertions.assertEquals(listOf("b", "a"), draft.entries.map { it.id.key })
    }

    @Test
    fun `the draft copies binary content`() {
        val content = byteArrayOf(1, 2, 3)
        draft.put("structure", NamespacedKey("test", "a"), content)
        content[0] = 9

        Assertions.assertEquals(1, draft.entries.single().content[0])
    }
}
