package com.willfp.eco.internal.spigot.datapack

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class PackPublisherTests {
    @TempDir
    lateinit var root: File

    private fun packDir() = File(File(root, "datapacks"), "eco_testplugin")

    private fun files(vararg pairs: Pair<String, String>) =
        pairs.associate { it.first to it.second.toByteArray() }

    @Test
    fun `publishing writes every file`() {
        val publisher = PackPublisher(packDir())

        val outcome = publisher.publish(
            files(
                "pack.mcmeta" to "{}",
                "data/test/worldgen/biome/thing.json" to """{"a": 1}"""
            )
        )

        Assertions.assertEquals(PublishOutcome.Written, outcome)
        Assertions.assertEquals(
            """{"a": 1}""",
            File(packDir(), "data/test/worldgen/biome/thing.json").readText()
        )
    }

    @Test
    fun `republishing identical content is unchanged`() {
        val publisher = PackPublisher(packDir())
        val content = files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to "{}")

        Assertions.assertEquals(PublishOutcome.Written, publisher.publish(content))
        Assertions.assertEquals(PublishOutcome.Unchanged, publisher.publish(content))
    }

    @Test
    fun `changed content republishes`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to "{}"))

        Assertions.assertEquals(
            PublishOutcome.Written,
            publisher.publish(files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to """{"b": 1}"""))
        )
    }

    @Test
    fun `removed entries disappear from the pack`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to "{}"))
        publisher.publish(files("pack.mcmeta" to "{}"))

        Assertions.assertFalse(File(packDir(), "data/test/recipe/a.json").exists())
    }

    @Test
    fun `a failed write leaves the live pack untouched`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to "original"))

        val outcome = publisher.publish(files("pack.mcmeta" to "{}", "../escape.json" to "evil"))

        Assertions.assertInstanceOf(PublishOutcome.Failed::class.java, outcome)
        Assertions.assertEquals("original", File(packDir(), "data/test/recipe/a.json").readText())
        Assertions.assertFalse(File(root, "datapacks/escape.json").exists())
    }

    @Test
    fun `no staging or backup directories are left behind`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}"))
        publisher.publish(files("pack.mcmeta" to """{"a": 1}"""))
        publisher.publish(files("pack.mcmeta" to "{}", "../escape.json" to "evil"))

        val leftovers = File(root, "datapacks").listFiles().orEmpty().filter { it.name.startsWith(".") }
        Assertions.assertTrue(leftovers.isEmpty(), "Left behind: ${leftovers.map { it.name }}")
    }

    @Test
    fun `deleting removes the pack`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}"))

        Assertions.assertEquals(PublishOutcome.Written, publisher.delete())
        Assertions.assertFalse(packDir().exists())
    }

    @Test
    fun `deleting a pack that does not exist is unchanged`() {
        Assertions.assertEquals(PublishOutcome.Unchanged, PackPublisher(packDir()).delete())
    }

    @Test
    fun `reading disk returns pack-relative paths`() {
        val publisher = PackPublisher(packDir())

        publisher.publish(files("pack.mcmeta" to "{}", "data/test/recipe/a.json" to "{}"))

        Assertions.assertEquals(
            setOf("pack.mcmeta", "data/test/recipe/a.json"),
            publisher.readDisk().keys
        )
    }
}
