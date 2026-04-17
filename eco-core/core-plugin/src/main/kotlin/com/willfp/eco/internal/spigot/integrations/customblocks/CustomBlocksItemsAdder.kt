package com.willfp.eco.internal.spigot.integrations.customblocks

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.block.Block
import java.util.function.Predicate

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

            val block = dev.lone.itemsadder.api.CustomBlock.getInstance(internalId) ?: return null
            val id = block.id
            val namespacedKey = namespacedKeyOf("itemsadder", key.lowercase().replace(":", "__"))
            return CustomBlock(
                namespacedKey,
                Predicate { test: Block ->
                    val customBlock =
                        dev.lone.itemsadder.api.CustomBlock.byAlreadyPlaced(test) ?: return@Predicate false
                    customBlock.id.equals(id, ignoreCase = true)
                },
                { location ->
                    dev.lone.itemsadder.api.CustomBlock.place(id, location)
                    location.block
                }
            )
        }
    }
}
