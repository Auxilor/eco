package com.willfp.eco.internal.config.lang

import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * The language set in eco's config.yml is loaded when the plugin has it, and english otherwise;
 * whatever the owner typed, a plugin always comes up with a lang file.
 */
class LangSelectTests {
    private val log = CapturingLogger()

    @TempDir
    lateinit var path: Path

    private val dir: File
        get() = path.toFile()

    private val plugin by lazy { pluginIn(path, log.logger) }

    private fun onDisk(language: String, contents: String) {
        File(dir, "lang").mkdirs()
        File(dir, "lang/$language.yml").writeText(contents)
    }

    private fun englishAndSpanish() {
        onDisk("english", "a: hi")
        onDisk("spanish", "a: hola")
    }

    @Test
    fun `requested language is loaded`() {
        englishAndSpanish()

        assertEquals("hola", LangFiles.select(plugin, "spanish", false).getString("a"))
        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }

    @Test
    fun `selector is case and whitespace insensitive`() {
        englishAndSpanish()

        assertEquals("hola", LangFiles.select(plugin, " SPANISH ", false).getString("a"))
    }

    @Test
    fun `owner file names are matched case insensitively`() {
        onDisk("english", "a: hi")
        onDisk("German", "a: hallo")

        assertEquals("hallo", LangFiles.select(plugin, "german", false).getString("a"))
    }

    @Test
    fun `blank selector means english`() {
        englishAndSpanish()

        assertEquals("hi", LangFiles.select(plugin, "", false).getString("a"))
        assertEquals("hi", LangFiles.select(plugin, null, false).getString("a"))
        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }

    @Test
    fun `missing language falls back to english with a warning`() {
        englishAndSpanish()

        assertEquals("hi", LangFiles.select(plugin, "russian", false).getString("a"))
        assertTrue(log.warnings.any { "russian" in it }, log.warnings.toString())
    }

    @Test
    fun `malformed selected language falls back to english`() {
        englishAndSpanish()
        onDisk("broken", "a: [unclosed")

        assertEquals("hi", LangFiles.select(plugin, "broken", false).getString("a"))
        assertTrue(log.warnings.any { "broken" in it }, log.warnings.toString())
    }

    @Test
    fun `no language files at all gives an empty english`() {
        assertDoesNotThrow { LangFiles.select(plugin, "english", false) }

        assertTrue(File(dir, "lang/english.yml").isFile)
    }

    @Test
    fun `migrated but not selected warns`() {
        englishAndSpanish()
        onDisk("existing", "a: custom")

        LangFiles.select(plugin, "english", true)

        assertTrue(log.warnings.any { "existing.yml" in it }, log.warnings.toString())
    }

    @Test
    fun `migrated and selected is silent`() {
        englishAndSpanish()
        onDisk("existing", "a: custom")

        assertEquals("custom", LangFiles.select(plugin, "existing", true).getString("a"))
        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }
}
