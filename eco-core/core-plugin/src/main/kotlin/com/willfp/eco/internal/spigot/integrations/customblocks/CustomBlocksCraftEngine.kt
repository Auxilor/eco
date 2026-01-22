package com.willfp.eco.internal.spigot.integrations.customblocks

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.CustomBlock
import com.willfp.eco.core.blocks.TestableBlock
import com.willfp.eco.core.blocks.provider.BlockProvider
import com.willfp.eco.core.integrations.customblocks.CustomBlocksIntegration
import com.willfp.eco.util.namespacedKeyOf
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent
import net.momirealms.craftengine.core.util.Key
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.function.Predicate

class CustomBlocksCraftEngine(
    private val plugin: EcoPlugin
) : CustomBlocksIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "CraftEngine"
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onBlockRegister(event: CraftEngineReloadEvent) {
        Blocks.registerBlockProvider(CraftEngineProvider())
    }

    private class CraftEngineProvider : BlockProvider("craftengine") {
        override fun provideForKey(key: String): TestableBlock? {
            val split = key.split(":").toMutableList()

            if (split.size < 2) {
                return null
            }

            val namespace = split[0]
            val value = split[1]

            val id = Key.of(namespace, value)
            val blockId = CraftEngineBlocks.byId(id) ?: return null
            val namespacedKey = namespacedKeyOf("craftengine", key.lowercase().replace(":", "__"))

            return CustomBlock(
                namespacedKey,
                Predicate { test: Block ->
                    if (!CraftEngineBlocks.isCustomBlock(test)) {
                        return@Predicate false
                    }
                    val immutableBlockState = CraftEngineBlocks.getCustomBlockState(test) ?: return@Predicate false
                    return@Predicate immutableBlockState.owner().value().id().equals(blockId.id())
                },
                { location ->
                    CraftEngineBlocks.place(location, id, true)
                    location.block
                }
            )
        }

    }
}