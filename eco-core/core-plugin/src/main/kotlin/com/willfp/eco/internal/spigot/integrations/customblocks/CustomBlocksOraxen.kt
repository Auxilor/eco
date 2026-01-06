package com.willfp.eco.internal.spigot.integrations.customblocks

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.namespacedKeyOf
import io.th0rgal.oraxen.api.OraxenBlocks
import io.th0rgal.oraxen.api.OraxenItems
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomBlocksOraxen(
    private val plugin: EcoPlugin
) : CustomBlocksIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onItemRegister(event: OraxenItemsLoadedEvent) {
        Blocks.registerBlockProvider(OraxenProvider())
    }

    private class OraxenProvider : BlockProvider("oraxen") {
        override fun provideForKey(key: String): TestableBlock? {
            // The key
            if (!OraxenBlocks.isOraxenBlock(key)) {
                return null
            }

            if (!OraxenItems.exists(key)) {
                return null
            }

            val namespacedKey = namespacedKeyOf("oraxen", key)

            return CustomBlock(
                namespacedKey,
                {
                    OraxenBlocks.isOraxenBlock(it) &&
                            key.equals(OraxenBlocks.getOraxenBlock(it.location).itemID, ignoreCase = true)
                },
                { location ->
                    OraxenBlocks.place(key, location)
                    location.block
                }
            )
        }
    }
}
