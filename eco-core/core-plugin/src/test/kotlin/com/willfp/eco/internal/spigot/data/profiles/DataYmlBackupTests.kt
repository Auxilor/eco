package com.willfp.eco.internal.spigot.data.profiles

import java.io.File
import java.nio.file.Files
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The migration rewrites every profile in the store, so the backup is the only way back. It is
 * therefore a precondition of the migration rather than a convenience, and its rotation matters:
 * clobbering an earlier .bak with a half-migrated file would destroy the thing it exists to save.
 */
class DataYmlBackupTests {
    private fun folderWithDataYml(contents: String = "player:\n  test: 1\n"): File {
        val folder = Files.createTempDirectory("eco-backup-test").toFile()
        File(folder, "data.yml").writeText(contents)
        return folder
    }

    @Test
    fun `the first backup is written as data yml bak`() {
        val folder = folderWithDataYml()

        val backup = DataYmlBackup.backup(folder)

        assertEquals(File(folder, "data.yml.bak"), backup)
        assertEquals("player:\n  test: 1\n", backup?.readText())
    }

    @Test
    fun `the source file is copied rather than moved`() {
        val folder = folderWithDataYml()

        DataYmlBackup.backup(folder)

        assertTrue(File(folder, "data.yml").exists(), "data.yml is still the migration's read source")
    }

    @Test
    fun `an existing backup is not clobbered`() {
        val folder = folderWithDataYml()
        File(folder, "data.yml.bak").writeText("older backup\n")

        val backup = DataYmlBackup.backup(folder) { 1_700_000_000_000 }

        assertEquals(File(folder, "data.yml.1700000000000.bak"), backup)
        assertEquals("older backup\n", File(folder, "data.yml.bak").readText())
        assertEquals("player:\n  test: 1\n", backup?.readText())
    }

    @Test
    fun `a missing data yml means no backup`() {
        val folder = Files.createTempDirectory("eco-backup-test").toFile()

        assertNull(DataYmlBackup.backup(folder))
    }

    @Test
    fun `an unreadable source means no backup`() {
        val folder = folderWithDataYml()
        // A directory where data.yml should be is the portable way to make the copy fail.
        File(folder, "data.yml").delete()
        File(folder, "data.yml").mkdir()

        assertNull(DataYmlBackup.backup(folder))
    }

    @Test
    fun `a second rotation uses a fresh timestamp`() {
        val folder = folderWithDataYml()
        File(folder, "data.yml.bak").writeText("older backup\n")

        val first = DataYmlBackup.backup(folder) { 1 }
        val second = DataYmlBackup.backup(folder) { 2 }

        assertNotNull(first)
        assertEquals(File(folder, "data.yml.2.bak"), second)
    }
}
