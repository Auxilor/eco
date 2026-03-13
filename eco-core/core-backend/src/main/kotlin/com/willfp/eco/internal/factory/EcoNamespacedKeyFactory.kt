package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.factory.NamespacedKeyFactory
import org.bukkit.NamespacedKey

class EcoNamespacedKeyFactory(private val plugin: EcoPlugin) : NamespacedKeyFactory {
    override fun create(key: String): NamespacedKey {
        return NamespacedKey(plugin.id, key)
    }
}
