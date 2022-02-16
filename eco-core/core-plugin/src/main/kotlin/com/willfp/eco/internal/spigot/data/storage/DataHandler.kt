package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import org.bukkit.NamespacedKey
import java.util.UUID

interface DataHandler {
    fun save()
    fun saveAll(uuids: Iterable<UUID>)

    fun runPostInit() {

    }

    fun savePlayer(uuid: UUID) {
        saveKeysFor(uuid, PersistentDataKey.values())
    }

    fun <T> write(uuid: UUID, key: NamespacedKey, value: T)
    fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>)
    fun <T> read(uuid: UUID, key: NamespacedKey): T?
}