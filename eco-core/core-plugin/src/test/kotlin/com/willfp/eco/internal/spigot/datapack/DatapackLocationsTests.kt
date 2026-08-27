package com.willfp.eco.internal.spigot.datapack

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class DatapackLocationsTests {
    @TempDir
    lateinit var root: File

    private fun properties(content: String) = File(root, "server.properties").apply { writeText(content) }

    @Test
    fun `the level name comes from server properties`() {
        // Not Bukkit#getWorlds()[0], which is wrong when the first world's folder is
        // dimension-nested.
        Assertions.assertEquals("survival", DatapackLocations.levelName(properties("level-name=survival\n")))
    }

    @Test
    fun `a missing server properties falls back to world`() {
        Assertions.assertEquals("world", DatapackLocations.levelName(File(root, "nope.properties")))
    }

    @Test
    fun `a blank level name falls back to world`() {
        Assertions.assertEquals("world", DatapackLocations.levelName(properties("level-name=\n")))
    }

    @Test
    fun `comments and other keys are ignored`() {
        val file = properties("#comment\nmotd=hello\nlevel-name=my_world\nonline-mode=true\n")

        Assertions.assertEquals("my_world", DatapackLocations.levelName(file))
    }

    @Test
    fun `the datapacks directory sits inside the level folder`() {
        val container = File(root, "container")

        Assertions.assertEquals(
            File(File(container, "survival"), "datapacks"),
            DatapackLocations.datapacksDir(container, properties("level-name=survival\n"))
        )
    }

    @Test
    fun `pack names are prefixed and lowercased`() {
        Assertions.assertEquals("eco_ecoitems", DatapackLocations.packName("EcoItems"))
    }
}
