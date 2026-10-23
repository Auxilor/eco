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
    fun `shipped languages copied out`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: hola"))

        assertEquals("a: hola", File(dir, "lang/spanish.yml").readText().trim())
        assertEquals("a: hi", File(dir, "lang/english.yml").readText().trim())
    }

    @Test
    fun `user edits are not overwritten`() {
        onDisk(language = "english", contents = "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi"))

        assertEquals("custom", reload("english").getString("a"))
    }

    @Test
    fun `keys fill from shipped file`() {
        onDisk(language = "spanish", contents = "a: hola")

        LangFiles.sync(
            plugin,
            MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola\nb: adios")
        )

        assertEquals("adios", reload("spanish").getString("b"))
        assertTrue(log.warnings.none { "spanish" in it }, log.warnings.toString())
    }

    @Test
    fun `existing fills from english`() {
        onDisk(language = "existing", contents = "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertEquals("custom", reload("existing").getString("a"))
        assertEquals("bye", reload("existing").getString("b"))
        assertTrue(log.warnings.any { "existing.yml" in it && "b" in it }, log.warnings.toString())
    }

    @Test
    fun `custom language fills english`() {
        onDisk(language = "german", contents = "a: hallo")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertEquals("hallo", reload("german").getString("a"))
        assertEquals("bye", reload("german").getString("b"))
    }

    @Test
    fun `lagging translation fills`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        assertEquals("hola", reload("spanish").getString("a"))
        assertEquals("bye", reload("spanish").getString("b"))
    }

    @Test
    fun `unused keys are kept`() {
        onDisk(language = "existing", contents = "a: x\nold: y")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi"))

        assertEquals("y", reload("existing").getString("old"))
    }

    @Test
    fun `incomplete shipped file warns`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        assertTrue(
            log.warnings.any { "spanish" in it && "b" in it && "jar" in it },
            log.warnings.toString()
        )
    }

    @Test
    fun `complete shipped files silent`() {
        LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: hola"))

        assertTrue(log.warnings.isEmpty(), log.warnings.toString())
    }

    @Test
    fun `malformed file is skipped`() {
        onDisk(language = "broken", contents = "a: [unclosed")

        assertDoesNotThrow { LangFiles.sync(plugin, MapLangSource("english" to "a: hi")) }

        assertEquals("a: [unclosed", File(dir, "lang/broken.yml").readText())
        assertTrue(log.warnings.any { "broken.yml" in it }, log.warnings.toString())
    }

    @Test
    fun `junk in lang folder is ignored`() {
        onDisk(language = "english", contents = "a: hi")
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
    fun `sync registers no configs`() {
        onDisk(language = "existing", contents = "a: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye", "spanish" to "a: hola"))

        verify(exactly = 0) { plugin.configHandler.addConfig(any()) }
    }

    @Test
    fun `header comments survive a fill`() {
        onDisk(language = "existing", contents = "# My server's messages\n\na: custom")

        LangFiles.sync(plugin, MapLangSource("english" to "a: hi\nb: bye"))

        assertTrue(File(dir, "lang/existing.yml").readText().startsWith("# My server's messages"))
    }

    @Test
    fun `malformed shipped is skipped`() {
        assertDoesNotThrow {
            LangFiles.sync(plugin, MapLangSource("english" to "a: hi", "spanish" to "a: [unclosed"))
        }

        assertEquals("a: hi", File(dir, "lang/english.yml").readText().trim())
        assertTrue(!File(dir, "lang/spanish.yml").exists(), "a broken shipped file shouldn't be copied out")
        assertTrue(log.warnings.any { "spanish" in it }, log.warnings.toString())
    }
}
