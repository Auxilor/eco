package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID

interface DataHandler {
    /**
     * Read value from a key.
     */
    fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T?

    /**
     * Write value to a key.
     */
    fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T)

    /**
     * Save a set of keys for a given UUID.
     */
    fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>)

    // Everything below this are methods that are only needed for certain implementations.

    fun save() {

    }

    fun categorize(key: PersistentDataKey<*>, category: KeyRegistry.KeyCategory) {

    }

    fun initialize() {

    }
}
