@file:JvmName("PlayerbaseOperations")

package com.willfp.eco.core.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.logging.Level

/**
 * Run an action over every profile the server has saved data for, off the main thread.
 *
 * Intended for admin "all players" commands - resets, migrations, recounts. Three things make
 * this preferable to iterating `Bukkit.getOfflinePlayers()`:
 *
 * - The uuids come from the configured data handler, not from `usercache.json`, so the set is
 *   the players who actually have data rather than everyone the server has ever seen.
 * - It runs off the main thread, so a large playerbase does not freeze the server.
 * - It does not load a [Profile] per uuid. `PlayerProfile.load` inserts into the profile
 *   handler's cache, which is only cleared when a player quits, so iterating a whole
 *   playerbase through `profile` retains one profile object per uuid for the lifetime of the
 *   server.
 *
 * The action runs off the main thread, so it must not call the Bukkit API. Profile writes are
 * safe; anything touching an entity, world, or inventory is not - hop back with the scheduler
 * for that.
 *
 * The work runs on the plugin's own async scheduler rather than on the common ForkJoinPool.
 * The pool is sized to `availableProcessors - 1` and is shared with every other library on the
 * server, so parking one of its threads on blocking database I/O for the length of a whole
 * playerbase scan would starve everything else using it.
 *
 * @param plugin     The plugin, used for its async scheduler and its logger.
 * @param batchSize  How many profiles to process before yielding. 500 is a sane default.
 * @param onProgress Called with (processed, total) after each batch, off the main thread.
 * @param action     The action to run per uuid.
 * @return A future completing when every profile has been processed.
 */
@JvmOverloads
fun forEachSavedProfile(
    plugin: EcoPlugin,
    batchSize: Int = 500,
    onProgress: (Int, Int) -> Unit = { _, _ -> },
    action: (UUID) -> Unit
): CompletableFuture<Void> = CompletableFuture.runAsync({
    val uuids = Eco.get().savedProfileUUIDs.toList()

    for ((index, uuid) in uuids.withIndex()) {
        try {
            action(uuid)
        } catch (e: Exception) {
            // One bad profile must not abort the whole run, but it must not vanish either.
            plugin.logger.log(Level.WARNING, "Failed processing profile $uuid", e)
        }

        if (batchSize > 0 && (index + 1) % batchSize == 0) {
            onProgress(index + 1, uuids.size)
            Thread.yield()
        }
    }

    onProgress(uuids.size, uuids.size)
}, Executor { plugin.scheduler.async().run(it) })
