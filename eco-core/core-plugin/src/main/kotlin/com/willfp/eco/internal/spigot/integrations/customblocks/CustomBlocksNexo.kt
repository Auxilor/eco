package com.willfp.eco.internal.spigot.integrations.customblocks

import com.nexomc.nexo.api.NexoBlocks
import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import com.nexomc.nexo.utils.drops.Drop
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.namespacedKeyOf
import kotlin.random.Random
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

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
            if (!NexoBlocks.isCustomBlock(key)) {
                return null
            }

            val item = NexoItems.itemFromId(key) ?: return null
            val id = NexoItems.idFromItem(item)
            val namespacedKey = namespacedKeyOf(namespace = "nexo", key = id.toString())

            val hardness = NexoBlocks.customBlockMechanic(id.toString())?.breakable?.hardness?.toFloat() ?: -1f

            return CustomBlock(
                namespacedKey,
                { testBlock ->
                    NexoBlocks.isCustomBlock(testBlock) &&
                            id.equals(NexoBlocks.customBlockMechanic(testBlock.location)?.itemID, ignoreCase = true)
                },
                { location ->
                    NexoBlocks.place(id, location)
                    location.block
                },
                hardness
            ) { worldBlock, tool ->
                NexoBlocks.customBlockMechanic(worldBlock.location)?.run {
                    rollDropsWithoutSpawning(breakable.drop, tool ?: ItemStack(Material.AIR))
                }
            }
        }

        private fun rollDropsWithoutSpawning(drop: Drop, tool: ItemStack): List<ItemStack> = when {
            !drop.canDrop(tool) -> emptyList()
            drop.isSilktouch && tool.containsEnchantment(Enchantment.SILK_TOUCH) ->
                listOfNotNull(NexoItems.itemFromId(drop.sourceID)?.build())
            else -> drop.loots
                .filter { Random.nextDouble() <= it.probability }
                .map { it.getItem(drop.fortuneMultiplier(tool)) }
        }
    }
}
