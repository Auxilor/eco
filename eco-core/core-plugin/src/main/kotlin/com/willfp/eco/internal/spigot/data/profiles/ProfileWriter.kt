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

                // Every profile the server writes to during a migration is remembered, so that
                // verification leaves it alone. A profile part-way out of data.yml also has two
                // live stores, so it is written to both: the copy skips what was written here,
                // and a server that stops mid-copy resumes from a data.yml that has the value
                // rather than from before it.
                val migration = handler.liveMigration

                if (migration != null) {
                    migration.noteWrite(request.uuid, request.key)

                    if (migration.isMigrating(request.uuid)) {
                        handler.dataYmlStore.write(request.uuid, request.key, value)
                    }
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
        // data.yml is only ever written to while a migration is carrying profiles out of it: the
        // dual-write below is what a half-finished copy resumes from on the next boot. Every other
        // boot has nothing to flush -- eco's own bookkeeping is in the database, and the profiles
        // are too -- and saving anyway would delete and rewrite the whole file on a timer, which
        // is what recreated a data.yml an operator had just watched eco retire.
        plugin.scheduler.global().runTimer(autosaveInterval, autosaveInterval) {
            if (handler.liveMigration != null) {
                plugin.dataYml.save()
            }
        }
    }

    private data class WriteRequest<T>(val uuid: UUID, val key: PersistentDataKey<T>)
}

val PersistentDataKey<*>.isSavedLocally: Boolean
    get() = this.isLocal || EcoPlugin.getPlugin(this.key.namespace)?.isUsingLocalStorage == true
