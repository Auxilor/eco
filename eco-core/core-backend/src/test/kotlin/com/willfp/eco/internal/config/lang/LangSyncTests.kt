package com.willfp.eco.internal.config.lang

import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.internal.config.EcoLoadableConfig
import io.mockk.verify
import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * Every language file on disk ends up with every english key: from its own shipped copy where
 * there is one, otherwise from english with a warning so the owner knows those lines are english.
 */
class LangSyncTests {
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

    private fun reload(language: String) =
        EcoLoadableConfig(ConfigType.YAML, language, plugin, "lang/", LangSyncTests::class.java, false)

    @Test
    fun `shipped languages are copied out`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: hola"))

        assertEquals("a: hola", File(dir, "lang/spanish.yml").readText().trim())
        assertEquals("a: hi", File(dir, "lang/english.yml").readText().trim())
    }

    @Test
    fun `user edits are not overwritten`() {
        onDisk("english", "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi"))

        assertEquals("custom", reload("english").getString("a"))
    }

    @Test
    fun `new keys come from the matching shipped file silently`() {
        onDisk("spanish", "a: hola")

        LangFiles.sync(
            plugin,
            MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola\nb: adios")
        )

        assertEquals("adios", reload("spanish").getString("b"))
        assertTrue(log.warnings.none { "spanish" in it }, log.warnings.toString())
    }

    @Test
    fun `existing yml is filled from english with a warning`() {
        onDisk("existing", "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertEquals("custom", reload("existing").getString("a"))
        assertEquals("bye", reload("existing").getString("b"))
        assertTrue(log.warnings.any { "existing.yml" in it && "b" in it }, log.warnings.toString())
    }

    @Test
    fun `owner made language is filled from english`() {
        onDisk("german", "a: hallo")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertEquals("hallo", reload("german").getString("a"))
        assertEquals("bye", reload("german").getString("b"))
    }

    @Test
    fun `lagging translation is filled from english`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        assertEquals("hola", reload("spanish").getString("a"))
        assertEquals("bye", reload("spanish").getString("b"))
    }

    @Test
    fun `unused keys are kept`() {
        onDisk("existing", "a: x\nold: y")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi"))

        assertEquals("y", reload("existing").getString("old"))
    }

    @Test
    fun `dev check warns about shipped files missing english keys`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        assertTrue(
            log.warnings.any { "spanish" in it && "b" in it && "jar" in it },
            log.warnings.toString()
        )
    }

    @Test
    fun `complete shipped files produce no warnings`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: hola"))

        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }

    @Test
    fun `malformed file is skipped with a warning`() {
        onDisk("broken", "a: [unclosed")

        assertDoesNotThrow { LangFiles.sync(plugin, MapLangSource("english" to "a: hi")) }

        assertEquals("a: [unclosed", File(dir, "lang/broken.yml").readText())
        assertTrue(log.warnings.any { "broken.yml" in it }, log.warnings.toString())
    }

    @Test
    fun `junk in lang folder is ignored`() {
        onDisk("english", "a: hi")
        File(dir, "lang/README.txt").writeText("")
        File(dir, "lang/spanish.yml.bak").writeText("")
        File(dir, "lang/sub").mkdirs()
        File(dir, "lang/sub/x.yml").writeText("")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi"))

        assertEquals("", File(dir, "lang/README.txt").readText())
        assertEquals("", File(dir, "lang/spanish.yml.bak").readText())
        assertEquals("", File(dir, "lang/sub/x.yml").readText())
        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }

    @Test
    fun `sync registers nothing with the config handler`() {
        onDisk("existing", "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        verify(exactly = 0) { plugin.configHandler.addConfig(any()) }
    }

    @Test
    fun `header comments survive a fill`() {
        onDisk("existing", "# My server's messages\n\na: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertTrue(File(dir, "lang/existing.yml").readText().startsWith("# My server's messages"))
    }

    @Test
    fun `malformed shipped language is skipped with a warning`() {
        assertDoesNotThrow {
            LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: [unclosed"))
        }

        assertEquals("a: hi", File(dir, "lang/english.yml").readText().trim())
        assertTrue(!File(dir, "lang/spanish.yml").exists(), "a broken shipped file shouldn't be copied out")
        assertTrue(log.warnings.any { "spanish" in it }, log.warnings.toString())
    }
}
