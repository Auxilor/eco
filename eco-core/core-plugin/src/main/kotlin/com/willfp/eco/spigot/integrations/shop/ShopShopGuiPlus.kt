package com.willfp.eco.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopWrapper
import com.willfp.eco.core.items.Items
import net.brcdev.shopgui.ShopGuiPlusApi
import net.brcdev.shopgui.provider.item.ItemProvider
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class ShopShopGuiPlus : ShopWrapper {
    override fun registerEcoProvider() {
        ShopGuiPlusApi.registerItemProvider(EcoShopGuiPlusProvider())
    }

    class EcoShopGuiPlusProvider : ItemProvider("eco") {
        override fun isValidItem(itemStack: ItemStack?): Boolean {
            itemStack ?: return false

            return Items.isCustomItem(itemStack)
        }

        override fun loadItem(configurationSection: ConfigurationSection): ItemStack? {
            val id = configurationSection.getString("eco")
            return if (id == null) null else Items.lookup(id)?.item
        }

        override fun compare(itemStack1: ItemStack, itemStack2: ItemStack): Boolean {
            return Items.getCustomItem(itemStack1)?.key == Items.getCustomItem(itemStack2)?.key
        }
    }
}