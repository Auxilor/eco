package com.willfp.eco.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.util.NamespacedKeyUtils
import dev.lone.itemsadder.api.CustomStack
import dev.lone.itemsadder.api.ItemsAdder
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsItemsAdder(
    private val plugin: EcoPlugin
) : CustomItemsWrapper {
    override fun registerAllItems() {
        plugin.scheduler.runLater({
            for (item in ItemsAdder.getAllItems()) {
                val stack = item.itemStack
                val id = item.id
                val key = NamespacedKeyUtils.create("itemsadder", id.lowercase())
                CustomItem(
                    key,
                    Predicate { test: ItemStack ->
                        val customStack = CustomStack.byItemStack(test) ?: return@Predicate false
                        customStack.id.equals(id, ignoreCase = true)
                    },
                    stack
                ).register()
            }
        }, 2)
    }

    override fun getPluginName(): String {
        return "ItemsAdder"
    }
}