package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import org.bukkit.NamespacedKey

class EcoKeyRegistry : KeyRegistry {
    private val registry = mutableMapOf<NamespacedKey, PersistentDataKey<*>>()

    override fun registerKey(key: PersistentDataKey<*>) {
        if (this.registry.containsKey(key.key)) {
            this.registry.remove(key.key)
        }

        validateKey(key)

        this.registry[key.key] = key
    }

    override fun getRegisteredKeys(): MutableSet<PersistentDataKey<*>> {
        return registry.values.toMutableSet()
    }

    private fun <T> validateKey(key: PersistentDataKey<T>) {
        val default = key.defaultValue

        when (key.type) {
            PersistentDataKeyType.INT -> if (default !is Int) {
                throw IllegalArgumentException("Invalid Data Type! Should be Int")
            }
            PersistentDataKeyType.DOUBLE -> if (default !is Double) {
                throw IllegalArgumentException("Invalid Data Type! Should be Double")
            }
            PersistentDataKeyType.BOOLEAN -> if (default !is Boolean) {
                throw IllegalArgumentException("Invalid Data Type! Should be Boolean")
            }
            PersistentDataKeyType.STRING -> if (default !is String) {
                throw IllegalArgumentException("Invalid Data Type! Should be String")
            }
            PersistentDataKeyType.STRING_LIST -> if (default !is List<*> || default.firstOrNull() !is String?) {
                throw IllegalArgumentException("Invalid Data Type! Should be String List")
            }

            else -> throw NullPointerException("Null value found!")
        }
    }

    override fun getKeyFrom(namespacedKey: NamespacedKey): PersistentDataKey<*>? {
        return registry[namespacedKey]
    }
}
