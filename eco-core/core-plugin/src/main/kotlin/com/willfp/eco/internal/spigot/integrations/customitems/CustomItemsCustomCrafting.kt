@file:Suppress("DEPRECATION")

package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import me.wolfyscript.utilities.util.NamespacedKey
import me.wolfyscript.utilities.util.Registry
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsCustomCrafting : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(CustomCraftingProvider())
    }

    override fun getPluginName(): String {
        return "CustomCrafting"
    }

    private class CustomCraftingProvider : ItemProvider("customcrafting") {
        override fun provideForKey(key: String): TestableItem? {
            val nKey = key.replace("customcrafting:", "", ignoreCase = true)
            val itemKey = NamespacedKey("customcrafting", nKey)
            val item = Registry.CUSTOM_ITEMS.get(itemKey) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("customcrafting", key)
            val stack = item.create(1)
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack =
                        me.wolfyscript.utilities.api.inventory.custom_items.CustomItem.getByItemStack(test)
                            ?: return@Predicate false
                    val iKey = customStack.namespacedKey ?: return@Predicate false
                    iKey.equals(key)
                },
                stack
            )
        }
    }
}