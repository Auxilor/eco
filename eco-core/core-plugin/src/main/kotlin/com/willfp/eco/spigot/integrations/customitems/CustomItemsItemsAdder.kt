package com.willfp.eco.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import dev.lone.itemsadder.api.CustomStack
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsItemsAdder : CustomItemsWrapper {
    override fun registerAllItems() {
        Items.registerItemProvider(ItemsAdderProvider())
    }

    override fun getPluginName(): String {
        return "ItemsAdder"
    }

    private class ItemsAdderProvider : ItemProvider("itemsadder") {
        override fun provideForKey(key: String): TestableItem? {
            val item = CustomStack.getInstance("itemsadder:$key") ?: return null
            val id = item.id
            val namespacedKey = NamespacedKeyUtils.create("itemsadder", key)
            val stack = item.itemStack
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack = CustomStack.byItemStack(test) ?: return@Predicate false
                    customStack.id.equals(id, ignoreCase = true)
                },
                stack
            )
        }

    }
}