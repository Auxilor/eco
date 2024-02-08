package com.willfp.eco.internal.spigot.integrations.customitems

import com.jojodmo.itembridge.ItemBridge
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.inventory.ItemStack

class CustomItemsItemBridge : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(ItemBridgeProvider())
    }

    override fun getPluginName(): String {
        return "ItemBridge"
    }

    private class ItemBridgeProvider : ItemProvider("itembridge") {
        override fun provideForKey(key: String): TestableItem? {

            val split = key.split(":").toMutableList()

            if (split.size < 2) {
                return null
            }

            val itemKey = split[0]

            val item = split[1]

            val stack = ItemBridge.getItemStack(itemKey, item) ?: kotlin.run {
                return null
            }

            return CustomItem(
                namespacedKeyOf("eco:${key.lowercase().replace(":", "__")}"),
                { test: ItemStack ->
                    ItemBridge.isItemStack(test, itemKey, item)
                },
                stack
            )
        }
    }
}
