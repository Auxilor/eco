package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import org.bukkit.NamespacedKey

class EcoKeyRegistry : KeyRegistry {
    private val registry = mutableMapOf<NamespacedKey, PersistentDataKey<*>>()
    private val categories = mutableMapOf<NamespacedKey, KeyRegistry.KeyCategory>()

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
        when (key.type) {
            PersistentDataKeyType.INT -> if (key.defaultValue !is Int) {
                throw IllegalArgumentException("Invalid Data Type! Should be Int")
            }
            PersistentDataKeyType.DOUBLE -> if (key.defaultValue !is Double) {
                throw IllegalArgumentException("Invalid Data Type! Should be Double")
            }
            PersistentDataKeyType.BOOLEAN -> if (key.defaultValue !is Boolean) {
                throw IllegalArgumentException("Invalid Data Type! Should be Boolean")
            }
            PersistentDataKeyType.STRING -> if (key.defaultValue !is String) {
                throw IllegalArgumentException("Invalid Data Type! Should be String")
            }

            else -> throw NullPointerException("Null value found!")
        }
    }

    override fun markKeyAs(key: PersistentDataKey<*>, category: KeyRegistry.KeyCategory) {
        categories[key.key] = category
        (Eco.getHandler().profileHandler as EcoProfileHandler).handler.categorize(key, category) // ew
    }

    override fun getKeyFrom(namespacedKey: NamespacedKey): PersistentDataKey<*>? {
        return registry[namespacedKey]
    }
}
