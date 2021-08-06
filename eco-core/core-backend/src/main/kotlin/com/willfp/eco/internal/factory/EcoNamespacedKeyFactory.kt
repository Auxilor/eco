package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.factory.NamespacedKeyFactory
import org.bukkit.NamespacedKey

class EcoNamespacedKeyFactory(plugin: EcoPlugin) : PluginDependent<EcoPlugin>(plugin), NamespacedKeyFactory {
    override fun create(key: String): NamespacedKey {
        return NamespacedKey(plugin, key)
    }
}