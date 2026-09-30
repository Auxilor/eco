package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * data.yml has no copy in the jar: nothing reads it until a server has one to migrate out of, so
 * there is nothing to seed a new one from. Creating a config whose resource is absent has to give
 * an empty file rather than throwing on the way up, or a server that has never had a data.yml
 * cannot boot.
 */
class MissingJarResourceTests {
    // Relaxed, so the config handler it registers itself with is a mock of its own.
    private fun pluginIn(dir: Path): PluginLike = mockk<PluginLike>(relaxed = true).also {
        every { it.dataFolder } returns dir.toFile()
    }

    @Test
    fun `a config with no resource in the jar is created empty`(@TempDir dir: Path) {
        val config = EcoLoadableConfig(
            ConfigType.YAML,
            "definitely-not-in-the-jar",
            pluginIn(dir),
            "",
            MissingJarResourceTests::class.java,
            false
        )

        val file = File(dir.toFile(), "definitely-not-in-the-jar.yml")

        assertTrue(file.isFile, "the config file should have been created")
        assertEquals("", file.readText(), "a config with no jar resource should start empty")
        assertTrue(config.getKeys(true).isEmpty(), "an empty config should have no keys")
    }

    @Test
    fun `a config that does ship a resource is still seeded from it`(@TempDir dir: Path) {
        EcoLoadableConfig(
            ConfigType.YAML,
            "seeded",
            pluginIn(dir),
            "",
            MissingJarResourceTests::class.java,
            false
        )

        val file = File(dir.toFile(), "seeded.yml")

        assertTrue(file.isFile)
        assertEquals("seeded-value: 42", file.readText().trim(), "the jar copy should have been used")
    }
}
