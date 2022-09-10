package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID

abstract class DataHandler(
    val type: HandlerType
) {
    /**
     * Read value from a key.
     */
    abstract fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T?

    /**
     * Write value to a key.
     */
    abstract fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T)

    /**
     * Save a set of keys for a given UUID.
     */
    abstract fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>)

    // Everything below this are methods that are only needed for certain implementations.

    open fun save() {

    }

    open fun initialize() {

    }
}
