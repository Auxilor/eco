package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import io.lumine.mythic.api.adapters.AbstractItemStack
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.drops.DropMetadata
import io.lumine.mythic.api.drops.IItemDrop
import io.lumine.mythic.bukkit.adapters.BukkitItemStack
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack


class CustomItemsMythicMobs(
    private val plugin: EcoPlugin
) : CustomItemsIntegration, Listener {
    init {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "MythicMobs"
    }

    @EventHandler
    fun onLoad(event: MythicDropLoadEvent) {
        val name = event.dropName
        if (name.equals("eco", ignoreCase = true)) {
            event.register(
                MythicMobsDrop(plugin, event.config)
            )
        }
    }

    private class MythicMobsDrop(
        private val plugin: EcoPlugin,
        itemConfig: MythicLineConfig
    ) : IItemDrop {
        private val id = itemConfig.getString(arrayOf("type", "t", "item", "i"), "eco")

        override fun getDrop(data: DropMetadata, v: Double): AbstractItemStack {
            val item = Items.lookup(id)
            if (item is EmptyTestableItem) {
                plugin.logger.warning("Item with ID $id is invalid, check your configs!")
                return BukkitItemStack(ItemStack(Material.AIR))
            }
            return BukkitItemStack(item.item.apply { amount = v.toInt() })
        }
    }
}

