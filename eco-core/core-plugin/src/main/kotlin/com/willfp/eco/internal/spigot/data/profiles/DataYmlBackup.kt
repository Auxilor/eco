package com.willfp.eco.internal.spigot.data.profiles

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Copies data.yml aside before a migration rewrites the store.
 *
 * The migration is the one operation that touches every profile at once, so this copy is the only
 * way back from a bad one -- which makes it a precondition rather than a convenience. A copy, not a
 * move: data.yml stays the migration's read source, and keeps eco's own bookkeeping afterwards.
 *
 * Note that DataYml rewrites the whole file on every boot, so this is a copy of the file as it sits
 * after that rewrite. The rewrite is content-lossless, so every value is identical, but the original
 * mtime and any hand-authored comments below the header are already gone by the time this runs.
 */
object DataYmlBackup {
    private val logger = Logger.getLogger("eco")

    fun backup(dataFolder: File, now: () -> Long = System::currentTimeMillis): File? {
        val source = File(dataFolder, "data.yml")

        if (!source.isFile) {
            logger.log(Level.SEVERE, "Cannot back up ${source.absolutePath}: it is not a readable file")
            return null
        }

        // An existing .bak is a previous migration's escape hatch, so it is never overwritten.
        val destination = File(dataFolder, "data.yml.bak").let {
            if (it.exists()) File(dataFolder, "data.yml.${now()}.bak") else it
        }

        return try {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES)
            logger.info("Backed up data.yml to ${destination.name}")
            destination
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Failed to back up data.yml to ${destination.name}", e)
            null
        }
    }
}
