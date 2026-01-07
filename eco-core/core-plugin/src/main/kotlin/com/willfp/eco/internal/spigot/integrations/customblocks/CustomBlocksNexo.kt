package com.willfp.eco.internal.spigot.integrations.customblocks

import com.nexomc.nexo.api.NexoBlocks
import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomBlocksNexo(
    private val plugin: EcoPlugin
) : CustomBlocksIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "Nexo"
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onBlockRegister(event: NexoItemsLoadedEvent) {
        Blocks.registerBlockProvider(NexoProvider())
    }

    private class NexoProvider : BlockProvider("nexo") {
        override fun provideForKey(key: String): TestableBlock? {
            // The key
            if (!NexoBlocks.isCustomBlock(key)) {
                return null
            }

            val item = NexoItems.itemFromId(key) ?: return null
            val id = NexoItems.idFromItem(item)
            val namespacedKey = NamespacedKeyUtils.create("nexo", id.toString())

            return CustomBlock(
                namespacedKey,
                {
                    NexoBlocks.isCustomBlock(it) &&
                            id.equals(NexoBlocks.customBlockMechanic(it.location)?.itemID, ignoreCase = true)
                },
                { location ->
                    NexoBlocks.place(key, location)
                    location.block
                }
            )
        }
    }
}
