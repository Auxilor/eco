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
    fun onBlockRegister(event: OraxenItemsLoadedEvent) {
        Blocks.registerBlockProvider(OraxenProvider())
    }

    private class OraxenProvider : BlockProvider("oraxen") {
        override fun provideForKey(key: String): TestableBlock? {
            // The key
            if (!OraxenBlocks.isOraxenBlock(key)) {
                return null
            }

            val item = OraxenItems.getItemById(key) ?: return null
            val id = OraxenItems.getIdByItem(item)
            val namespacedKey = namespacedKeyOf("oraxen", id)

            return CustomBlock(
                namespacedKey,
                {
                    OraxenBlocks.isOraxenBlock(it) &&
                            id.equals(OraxenBlocks.getOraxenBlock(it.location).itemID, ignoreCase = true)
                },
                { location ->
                    OraxenBlocks.place(id, location)
                    location.block
                }
            )
        }
    }
}
