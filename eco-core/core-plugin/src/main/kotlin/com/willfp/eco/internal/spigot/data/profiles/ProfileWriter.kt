package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

/*
The profile writer exists as an optimization to batch writes to the database.

This is necessary because values frequently change multiple times per tick,
and we don't want to write to the database every time a value changes.

Instead, we only commit the last value that was set every interval (default 1 tick).
 */


class ProfileWriter(
    private val plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
) {
    private val saveInterval = plugin.configYml.getInt("save-interval").toLong()
    private val autosaveInterval = plugin.configYml.getInt("autosave-interval").toLong()
    private val valuesToWrite = ConcurrentHashMap<WriteRequest<*>, Any>()

    /**
     * Called for every value committed, or null if nothing is listening.
     *
     * Runs inside the per-tick drain below, so a listener must not block, do I/O, or throw. This
     * is how the leaderboard service learns about value changes without polling the database.
     */
    @Volatile
    var onWrite: ((UUID, PersistentDataKey<*>, Any) -> Unit)? = null

    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        valuesToWrite[WriteRequest(uuid, key)] = value
    }

    fun startTickingSaves() {
        plugin.scheduler.global().runTimer(20, saveInterval) {
            val iterator = valuesToWrite.iterator()

            while (iterator.hasNext()) {
                val (request, value) = iterator.next()
                iterator.remove()

                val dataHandler = if (request.key.isSavedLocally) handler.localHandler else handler.defaultHandler

                // Pass the value to the data handler
                @Suppress("UNCHECKED_CAST")
                dataHandler.write(request.uuid, request.key as PersistentDataKey<Any>, value)

                // A profile part-way out of data.yml has two live stores, so it is written to
                // both: the copy skips what was written here, and a server that stops mid-copy
                // resumes from a data.yml that has the value rather than from before it.
                val migration = handler.liveMigration

                if (migration != null && migration.isMigrating(request.uuid)) {
                    migration.noteLiveWrite(request.uuid, request.key)
                    handler.dataYmlStore.write(request.uuid, request.key as PersistentDataKey<Any>, value)
                }

                // A broken listener must not stop the rest of the queue from being written: the
                // data itself is already committed above, and losing the drain would lose writes.
                try {
                    onWrite?.invoke(request.uuid, request.key, value)
                } catch (e: Exception) {
                    plugin.logger.log(Level.WARNING, "Failed to notify a listener of a profile write", e)
                }
            }
        }
    }

    fun startTickingAutosave() {
        // The local handler is a database that commits on write and reports that it does not
        // autosave, so this timer is data.yml's only flush -- for eco's own bookkeeping, which is
        // all that file still holds.
        plugin.scheduler.global().runTimer(autosaveInterval, autosaveInterval) {
            plugin.dataYml.save()
        }
    }

    private data class WriteRequest<T>(val uuid: UUID, val key: PersistentDataKey<T>)
}

val PersistentDataKey<*>.isSavedLocally: Boolean
    get() = this.isLocal || EcoPlugin.getPlugin(this.key.namespace)?.isUsingLocalStorage == true
