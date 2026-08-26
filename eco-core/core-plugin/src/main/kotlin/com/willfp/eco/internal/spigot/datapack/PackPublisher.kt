package com.willfp.eco.internal.spigot.datapack

import java.io.File
import java.io.IOException
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

/**
 * The outcome of a publish.
 */
sealed interface PublishOutcome {
    /** The built content was byte-identical to what was already on disk. Nothing was touched. */
    data object Unchanged : PublishOutcome

    /** The pack was replaced. */
    data object Written : PublishOutcome

    /** Nothing was written. The live pack is untouched. */
    data class Failed(val messages: List<String>) : PublishOutcome
}

/**
 * Writes a pack to disk, atomically.
 *
 * A half-written pack must never be visible to a boot, because a boot that reads one does not
 * degrade, it aborts. Hence: build everything in a staging directory first, swap it in as a single
 * rename, and keep a backup to roll back to if the swap fails halfway.
 *
 * Staging, backup and target all share a parent so the moves stay on one filesystem. Symlinks are
 * rejected outright rather than followed.
 */
class PackPublisher(
    private val packDir: File
) {
    /**
     * Build [files] into the pack directory.
     *
     * @param files Pack-relative path (using `/`) to content.
     */
    fun publish(files: Map<String, ByteArray>): PublishOutcome {
        rejectSymlinks()?.let { return PublishOutcome.Failed(listOf(it)) }

        if (matchesDisk(files)) {
            return PublishOutcome.Unchanged
        }

        val parent = packDir.parentFile
            ?: return PublishOutcome.Failed(listOf("Pack directory ${packDir.path} has no parent"))

        if (!parent.isDirectory && !parent.mkdirs()) {
            return PublishOutcome.Failed(listOf("Could not create ${parent.path}"))
        }

        val staging = File(parent, ".${packDir.name}.staging-${UUID.randomUUID()}")

        try {
            writeStaging(staging, files)
        } catch (e: IOException) {
            staging.deleteRecursively()
            return PublishOutcome.Failed(listOf("Could not stage pack: ${e.message}"))
        }

        return swap(staging, parent)
    }

    /**
     * Delete the pack directory entirely.
     */
    fun delete(): PublishOutcome {
        rejectSymlinks()?.let { return PublishOutcome.Failed(listOf(it)) }

        if (!packDir.exists()) {
            return PublishOutcome.Unchanged
        }

        val parent = packDir.parentFile
            ?: return PublishOutcome.Failed(listOf("Pack directory ${packDir.path} has no parent"))

        val backup = File(parent, ".${packDir.name}.backup-${UUID.randomUUID()}")

        return try {
            move(packDir, backup)
            backup.deleteRecursively()
            PublishOutcome.Written
        } catch (e: IOException) {
            PublishOutcome.Failed(listOf("Could not remove pack: ${e.message}"))
        }
    }

    /**
     * The current on-disk content, keyed by pack-relative path.
     */
    fun readDisk(): Map<String, ByteArray> {
        if (!packDir.isDirectory) {
            return emptyMap()
        }

        return packDir.walkTopDown()
            .filter { it.isFile }
            .associate { it.relativeTo(packDir).invariantPath() to it.readBytes() }
    }

    private fun swap(staging: File, parent: File): PublishOutcome {
        val backup = File(parent, ".${packDir.name}.backup-${UUID.randomUUID()}")
        var backedUp = false

        try {
            if (packDir.exists()) {
                move(packDir, backup)
                backedUp = true
            }

            move(staging, packDir)
        } catch (e: IOException) {
            staging.deleteRecursively()

            if (backedUp && !packDir.exists()) {
                runCatching { move(backup, packDir) }
            }

            backup.deleteRecursively()
            return PublishOutcome.Failed(listOf("Could not install pack: ${e.message}"))
        }

        backup.deleteRecursively()
        return PublishOutcome.Written
    }

    private fun writeStaging(staging: File, files: Map<String, ByteArray>) {
        if (!staging.mkdirs()) {
            throw IOException("Could not create staging directory ${staging.path}")
        }

        // Sorted so that a partially-failed write is at least reproducible.
        for (path in files.keys.sorted()) {
            val target = File(staging, path)

            if (!target.canonicalPath.startsWith(staging.canonicalPath + File.separator)) {
                throw IOException("Entry path '$path' escapes the pack directory")
            }

            target.parentFile?.mkdirs()
            target.writeBytes(files.getValue(path))
        }
    }

    private fun matchesDisk(files: Map<String, ByteArray>): Boolean {
        val onDisk = readDisk()

        if (onDisk.keys != files.keys) {
            return false
        }

        return onDisk.all { (path, content) -> content.contentEquals(files.getValue(path)) }
    }

    private fun rejectSymlinks(): String? {
        if (Files.isSymbolicLink(packDir.toPath())) {
            return "Refusing to write to ${packDir.path}: it is a symbolic link"
        }

        val parent = packDir.parentFile ?: return null

        if (Files.isSymbolicLink(parent.toPath())) {
            return "Refusing to write to ${packDir.path}: ${parent.path} is a symbolic link"
        }

        return null
    }

    private fun move(from: File, to: File) {
        try {
            Files.move(from.toPath(), to.toPath(), StandardCopyOption.ATOMIC_MOVE)
        } catch (e: AtomicMoveNotSupportedException) {
            Files.move(from.toPath(), to.toPath())
        }
    }

    private fun File.invariantPath() = this.path.replace(File.separatorChar, '/')
}
