package com.willfp.eco.internal.config.lang

import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

/**
 * A server from before languages existed has a hand-edited lang.yml. It has to end up in
 * lang/existing.yml without a single message being lost, including when a rollback has already
 * left an existing.yml behind.
 */
class LangMigrationTests {
    private val log = CapturingLogger()

    @Test
    fun `lang yml is moved to lang existing yml`(@TempDir path: Path) {
        val dir = path.toFile()
        File(dir, "lang.yml").writeText("a: custom")

        assertTrue(LangFiles.migrate(dir, log.logger))

        assertFalse(File(dir, "lang.yml").exists(), "lang.yml should be moved, not copied")
        assertEquals("a: custom", File(dir, "lang/existing.yml").readText().trim())
    }

    @Test
    fun `nothing to migrate returns false`(@TempDir path: Path) {
        assertFalse(LangFiles.migrate(path.toFile(), log.logger))
        assertTrue(log.warnings.isEmpty())
    }

    @Test
    fun `existing yml is never overwritten`(@TempDir path: Path) {
        val dir = path.toFile()
        File(dir, "lang").mkdirs()
        File(dir, "lang.yml").writeText("a: new")
        File(dir, "lang/existing.yml").writeText("a: old")
        File(dir, "lang/existing-2.yml").writeText("a: older")

        assertTrue(LangFiles.migrate(dir, log.logger))

        assertEquals("a: old", File(dir, "lang/existing.yml").readText().trim())
        assertEquals("a: older", File(dir, "lang/existing-2.yml").readText().trim())
        assertEquals("a: new", File(dir, "lang/existing-3.yml").readText().trim())
        assertTrue(log.warnings.any { "existing-3.yml" in it })
    }

    @Test
    fun `a move that fails leaves lang yml in place`(@TempDir path: Path) {
        val dir = path.toFile()
        File(dir, "lang.yml").writeText("a: custom")
        File(dir, "lang").writeText("not a directory")

        assertFalse(LangFiles.migrate(dir, log.logger))

        assertEquals("a: custom", File(dir, "lang.yml").readText().trim())
        assertTrue(log.warnings.any { "lang.yml" in it }, log.warnings.toString())
    }
}
