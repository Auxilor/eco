package com.willfp.eco.internal.factory

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.factory.MetadataValueFactory
import org.bukkit.metadata.FixedMetadataValue

class EcoMetadataValueFactory(private val plugin: EcoPlugin) : MetadataValueFactory {
    override fun create(value: Any): FixedMetadataValue {
        return FixedMetadataValue(plugin, value)
    }
}