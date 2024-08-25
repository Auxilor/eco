package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.keys.PersistentDataKey
import org.bukkit.NamespacedKey

object KeyRegistry {
    private val registry = mutableMapOf<NamespacedKey, PersistentDataKey<*>>()

    fun registerKey(key: PersistentDataKey<*>) {
        if (key.defaultValue == null) {
            throw IllegalArgumentException("Default value cannot be null!")
        }

        this.registry[key.key] = key
    }

    fun getRegisteredKeys(): Set<PersistentDataKey<*>> {
        return registry.values.toSet()
    }
}
