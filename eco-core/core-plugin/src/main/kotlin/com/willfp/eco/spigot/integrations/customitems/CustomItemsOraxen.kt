package com.willfp.eco.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.util.NamespacedKeyUtils
import io.th0rgal.oraxen.items.OraxenItems
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsOraxen : CustomItemsWrapper {
    override fun registerAllItems() {
        for (item in OraxenItems.getItems()) {
            val stack = item.build()
            val id: String = OraxenItems.getIdByItem(item)
            val key: NamespacedKey = NamespacedKeyUtils.create("oraxen", id.lowercase())
            CustomItem(
                key, Predicate { test: ItemStack ->
                    val oraxenId: String = OraxenItems.getIdByItem(test) ?: return@Predicate false
                    oraxenId.equals(id, ignoreCase = true)
                },
                stack
            ).register()
        }
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }
}