package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.drops.DropMetadata
import io.lumine.mythic.api.drops.IMultiDrop
import io.lumine.mythic.bukkit.adapters.BukkitItemStack
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent
import io.lumine.mythic.core.drops.Drop
import io.lumine.mythic.core.drops.LootBag
import io.lumine.mythic.core.drops.droppables.ItemDrop
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomItemsMythicMobs(
    private val plugin: EcoPlugin
) : CustomItemsWrapper, Listener {
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
    ) : Drop(itemConfig.line, itemConfig), IMultiDrop {
        private val id = itemConfig.getString(arrayOf("type", "t", "item", "i"), this.dropVar)

        override fun get(data: DropMetadata): LootBag {
            val bag = LootBag(data)

            val item = Items.lookup(id)
            if (item is EmptyTestableItem) {
                plugin.logger.warning("Item with ID $id is invalid, check your configs!")
                return bag
            }
            bag.add(data, ItemDrop(this.line, this.config, BukkitItemStack(item.item)))
            return bag
        }
    }
}
