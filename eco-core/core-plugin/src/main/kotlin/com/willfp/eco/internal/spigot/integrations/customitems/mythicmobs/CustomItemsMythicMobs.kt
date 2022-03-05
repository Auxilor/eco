package com.willfp.eco.internal.spigot.integrations.customitems.mythicmobs

import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack
import io.lumine.xikage.mythicmobs.drops.Drop
import io.lumine.xikage.mythicmobs.drops.DropMetadata
import io.lumine.xikage.mythicmobs.drops.IMultiDrop
import io.lumine.xikage.mythicmobs.drops.LootBag
import io.lumine.xikage.mythicmobs.drops.droppables.ItemDrop
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import org.bukkit.Bukkit

class CustomItemsMythicMobs(config: MythicLineConfig): Drop(config.line, config), IMultiDrop {

    val id: String

    init {
        id = config.getString(mutableListOf("type", "t", "item", "i").toTypedArray(), this.dropVar)
    }

    override fun get(data: DropMetadata): LootBag {
        val bag = LootBag(data)
        val item = Items.lookup(id)
        if (item is EmptyTestableItem) {
            Bukkit.getLogger().severe("Item $id is invalid, make sure to fix this on your side")
        }
        bag.add(data, ItemDrop(this.line, this.config as MythicLineConfig, BukkitItemStack(item.item)))
        return bag
    }
}