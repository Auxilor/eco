package com.willfp.eco.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import org.bukkit.NamespacedKey
import java.util.*

interface DataHandler {
    fun save() {

    }

    fun saveAll(uuids: Iterable<UUID>)

    fun updateKeys() {

    }

    fun savePlayer(uuid: UUID) {
        saveKeysForPlayer(uuid, PersistentDataKey.values())
    }

    fun <T> write(uuid: UUID, key: NamespacedKey, value: T)
    fun saveKeysForPlayer(uuid: UUID, keys: Set<PersistentDataKey<*>>)
    fun <T> read(uuid: UUID, key: NamespacedKey): T?
}