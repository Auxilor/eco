package com.willfp.eco.internal.spigot.integrations.customblocks

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.namespacedKeyOf
import dev.lone.itemsadder.api.CustomBlock as ItemsAdderBlock
import java.util.function.Predicate
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class CustomBlocksItemsAdder : CustomBlocksIntegration {
    override fun registerProvider() {
        Blocks.registerBlockProvider(ItemsAdderProvider())
    }

    override fun getPluginName(): String {
        return "ItemsAdder"
    }

    private class ItemsAdderProvider : BlockProvider("itemsadder") {
        override fun provideForKey(key: String): TestableBlock? {
            val internalId = if (key.contains(":")) key else "itemsadder:$key"

            val block = ItemsAdderBlock.getInstance(internalId) ?: return null
            val id = block.id
            val namespacedKey = namespacedKeyOf(namespace = "itemsadder", key = key.lowercase().replace(":", "__"))
            return CustomBlock(
                namespacedKey,
                Predicate { test: Block ->
                    val customBlock = ItemsAdderBlock.byAlreadyPlaced(test) ?: return@Predicate false
                    customBlock.id.equals(id, ignoreCase = true)
                },
                { location ->
                    ItemsAdderBlock.place(id, location)
                    location.block
                },
                -1f
            ) { worldBlock, tool ->
                ItemsAdderBlock.byAlreadyPlaced(worldBlock)?.let { placed ->
                    runCatching { placed.getLoot(tool ?: ItemStack(Material.AIR), true) }
                        .getOrElse { emptyList() }
                }
            }
        }
    }
}
