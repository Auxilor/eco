package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.factory.NamespacedKeyFactory
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.NamespacedKey

class EcoNamespacedKeyFactory(private val plugin: EcoPlugin) : NamespacedKeyFactory {
    override fun create(key: String): NamespacedKey {
        return NamespacedKeyUtils.create(plugin.id, key)
    }
}