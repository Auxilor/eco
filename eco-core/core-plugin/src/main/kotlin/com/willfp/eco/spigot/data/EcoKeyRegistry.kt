package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.keys.KeyRegistry
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.spigot.EcoSpigotPlugin
import org.bukkit.NamespacedKey

class EcoKeyRegistry(
    private val plugin: EcoSpigotPlugin
) : KeyRegistry {
    private val registry = mutableMapOf<NamespacedKey, PersistentDataKey<*>>()

    override fun registerKey(key: PersistentDataKey<*>) {
        if (this.registry.containsKey(key.key)) {
            this.registry.remove(key.key)
        }

        validateKey(key)

        this.registry[key.key] = key

        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).updateKeys()
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
}