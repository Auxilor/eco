package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID

interface DataHandler {
    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: Any)
    fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T?

    fun save() {

    }

    fun saveAll(uuids: Iterable<UUID>)

    fun categorize(key: PersistentDataKey<*>, category: KeyRegistry.KeyCategory) {

    }

    fun initialize() {

    }

    fun savePlayer(uuid: UUID) {
        saveKeysFor(uuid, PersistentDataKey.values())
    }

    fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>)
}