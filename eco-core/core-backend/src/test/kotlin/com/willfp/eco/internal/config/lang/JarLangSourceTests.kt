package com.willfp.eco.internal.config.lang

import java.io.File
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * Plugins ship their languages as lang/<name>.yml, and plugins built before languages existed
 * ship a single root lang.yml that has to keep working as english.
 */
class JarLangSourceTests {
    private fun jarIn(dir: Path, vararg entries: Pair<String, String>): File {
        val jar = File(dir.toFile(), "plugin.jar")
        JarOutputStream(jar.outputStream()).use { out ->
            for ((name, contents) in entries) {
                out.putNextEntry(JarEntry(name))
                out.write(contents.toByteArray(Charsets.UTF_8))
                out.closeEntry()
            }
        }
        return jar
    }

    @Test
    fun `lang folder entries become languages`(@TempDir dir: Path) {
        val source = JarLangSource(jarIn(dir, "lang/english.yml" to "a: 1", "lang/spanish.yml" to "a: uno"))

        assertEquals(setOf("english", "spanish"), source.languages)
        assertEquals("a: uno", source.read("spanish")?.trim())
    }

    @Test
    fun `root lang yml counts as english`(@TempDir dir: Path) {
        val source = JarLangSource(jarIn(dir, "lang.yml" to "a: root"))

        assertEquals(setOf("english"), source.languages)
        assertEquals("a: root", source.read("english")?.trim())
    }

    @Test
    fun `lang english yml wins over root lang yml`(@TempDir dir: Path) {
        val source = JarLangSource(jarIn(dir, "lang.yml" to "a: root", "lang/english.yml" to "a: folder"))

        assertEquals("a: folder", source.read("english")?.trim())
    }

    @Test
    fun `non yml and nested entries are ignored`(@TempDir dir: Path) {
        val source = JarLangSource(
            jarIn(
                dir,
                "lang/english.yml" to "a: 1",
                "lang/README.txt" to "hi",
                "lang/old/spanish.yml" to "a: uno",
                "other.yml" to "a: 2"
            )
        )

        assertEquals(setOf("english"), source.languages)
    }

    @Test
    fun `unknown language reads null`(@TempDir dir: Path) {
        val source = JarLangSource(jarIn(dir, "lang/english.yml" to "a: 1"))

        assertNull(source.read("klingon"))
    }

    @Test
    fun `a plugin file that isn't a jar gives no languages and a warning`(@TempDir dir: Path) {
        val log = CapturingLogger()

        val source = JarLangSource.open(dir.toFile(), log.logger)

        assertEquals(emptySet<String>(), source.languages)
        assertTrue(log.warnings.isNotEmpty())
    }
}
