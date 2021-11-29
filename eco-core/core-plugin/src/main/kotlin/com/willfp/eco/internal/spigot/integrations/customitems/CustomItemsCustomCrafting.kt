package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import dev.lone.itemsadder.api.CustomStack
import me.wolfyscript.utilities.util.NamespacedKey
import me.wolfyscript.utilities.util.Registry
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsCustomCrafting: CustomItemsWrapper {
    override fun registerAllItems() {
        Items.registerItemProvider(ItemsAdderProvider())
    }

    override fun getPluginName(): String {
        return "CustomCrafting"
    }

    private class ItemsAdderProvider : ItemProvider("customcrafting") {
        override fun provideForKey(key: String): TestableItem? {
            val nKey = key.replace("customcrafting:", "", ignoreCase = true)
            val itemKey = NamespacedKey("customcrafting", nKey)
            val item = Registry.CUSTOM_ITEMS.get(itemKey) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("customcrafting", key)
            val stack = item.itemStack
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack = CustomStack.byItemStack(test) ?: return@Predicate false
                    customStack.id.equals(nKey, ignoreCase = true)
                },
                stack
            )
        }

    }
}