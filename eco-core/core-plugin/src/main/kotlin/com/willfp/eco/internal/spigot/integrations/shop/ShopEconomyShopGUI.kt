package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.impl.PriceEconomy
import com.willfp.eco.core.price.impl.PriceFree
import me.gypopo.economyshopgui.api.EconomyShopGUIHook
import me.gypopo.economyshopgui.api.events.PreTransactionEvent
import me.gypopo.economyshopgui.util.Transaction
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class ShopEconomyShopGUI : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return EconomyShopGUISellEventListeners
    }

    override fun getUnitValue(itemStack: ItemStack, player: Player): Price {
        val shopItem = EconomyShopGUIHook.getShopItem(itemStack) ?: return PriceFree()

        return PriceEconomy(
            EconomyShopGUIHook.getItemSellPrice(shopItem, itemStack.clone().apply {
                amount = 1
            }, player)
        )
    }

    override fun isSellable(itemStack: ItemStack, player: Player): Boolean {
        val shopItem = EconomyShopGUIHook.getShopItem(itemStack) ?: return false
        return EconomyShopGUIHook.getItemSellPrice(shopItem, itemStack, player) > 0
    }

    object EconomyShopGUISellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: PreTransactionEvent) {
            if (!event.transactionType.mode.equals(Transaction.Mode.SELL.name, true)) {
                return
            }

            if (event.isCancelled) {
                return
            }

            val prices = event.items ?: mapOf(event.shopItem to event.amount)

            var total = 0.0

            for ((shopItem, amount) in prices) {
                val price = EconomyShopGUIHook.getItemSellPrice(shopItem, shopItem.itemToGive
                    .apply { this.amount = amount }, event.player)
                val ecoEvent = ShopSellEvent(event.player, PriceEconomy(price), shopItem.itemToGive
                    .apply { this.amount = amount })
                Bukkit.getPluginManager().callEvent(ecoEvent)
                total += ecoEvent.value.getValue(event.player) * ecoEvent.multiplier
            }

            event.price = total
        }
    }

    override fun getPluginName(): String {
        return "EconomyShopGUI"
    }
}
