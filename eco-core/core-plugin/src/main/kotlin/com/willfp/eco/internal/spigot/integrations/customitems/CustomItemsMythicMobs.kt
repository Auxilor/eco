package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.Drop
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import io.lumine.xikage.mythicmobs.drops.IMultiDrop
import io.lumine.xikage.mythicmobs.drops.LootBag
import io.lumine.xikage.mythicmobs.drops.droppables.ItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
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

    override fun registerAllItems() {
        // Do nothing.
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
        private val config: MythicLineConfig
    ) : Drop(config.line, config), IMultiDrop {
        private val id = config.getString(arrayOf("type", "t", "item", "i"), this.dropVar)

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
