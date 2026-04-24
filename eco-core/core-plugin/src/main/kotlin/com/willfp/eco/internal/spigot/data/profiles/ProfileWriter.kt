package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/*
The profile writer exists as an optimization to batch writes to the database.

This is necessary because values frequently change multiple times per tick,
and we don't want to write to the database every time a value changes.

Instead, we only commit the last value that was set every interval (default 1 tick).
 */


class ProfileWriter(
    private val plugin: EcoPlugin,
    private val handler: ProfileHandler
) {
    private val saveInterval = plugin.configYml.getInt("save-interval").toLong()
    private val autosaveInterval = plugin.configYml.getInt("autosave-interval").toLong()
    private val valuesToWrite = ConcurrentHashMap<WriteRequest<*>, Any>()

    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        valuesToWrite[WriteRequest(uuid, key)] = value
    }

    fun startTickingSaves() {
        plugin.scheduler.runTaskTimer(20, saveInterval) {
            val iterator = valuesToWrite.iterator()

            while (iterator.hasNext()) {
                val (request, value) = iterator.next()
                iterator.remove()

                val dataHandler = if (request.key.isSavedLocally) handler.localHandler else handler.defaultHandler

                // Pass the value to the data handler
                @Suppress("UNCHECKED_CAST")
                dataHandler.write(request.uuid, request.key as PersistentDataKey<Any>, value)
            }
        }
    }

    fun startTickingAutosave() {
        plugin.scheduler.runTaskTimer(autosaveInterval, autosaveInterval) {
            if (handler.localHandler.shouldAutosave()) {
                handler.localHandler.save()
            }
        }
    }

    private data class WriteRequest<T>(val uuid: UUID, val key: PersistentDataKey<T>)
}

val PersistentDataKey<*>.isSavedLocally: Boolean
    get() = this.isLocal || EcoPlugin.getPlugin(this.key.namespace)?.isUsingLocalStorage == true
