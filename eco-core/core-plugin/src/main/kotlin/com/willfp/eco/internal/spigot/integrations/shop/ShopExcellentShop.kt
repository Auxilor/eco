package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.impl.PriceEconomy
import com.willfp.eco.core.price.impl.PriceFree
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import su.nightexpress.nexshop.ShopAPI
import su.nightexpress.nexshop.api.shop.Transaction
import su.nightexpress.nexshop.api.shop.event.ShopTransactionEvent
import su.nightexpress.nexshop.api.shop.type.TradeType
import su.nightexpress.nexshop.shop.virtual.impl.VirtualShop

class ShopExcellentShop : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return ExcellentShopSellEventListeners
    }

    override fun getUnitValue(itemStack: ItemStack, player: Player): Price {
        val virtualShop = ShopAPI.getVirtualShop() ?: return PriceFree()
        val bestDeal = virtualShop.getBestProductFor(
            itemStack.clone().apply {
                amount = 1
            }, TradeType.SELL, player
        ) ?: return PriceFree()

        return PriceEconomy(
            bestDeal.sellPrice
        )
    }

    override fun isSellable(itemStack: ItemStack, player: Player): Boolean {
        val virtualShop = ShopAPI.getVirtualShop() ?: return false
        val bestDeal = virtualShop.getBestProductFor(
            itemStack, TradeType.SELL, player
        ) ?: return false
        return bestDeal.sellPrice > 0
    }

    object ExcellentShopSellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: ShopTransactionEvent) {
            if (event.shop !is VirtualShop) {
                return
            }

            if (event.transactionResult != Transaction.Result.SUCCESS) {
                return
            }

            val ecoEvent = ShopSellEvent(event.player, PriceEconomy(event.transaction.price), event.transaction.product.preview)
            Bukkit.getPluginManager().callEvent(ecoEvent)
            event.transaction.price = ecoEvent.value.getValue(event.player) * ecoEvent.multiplier
        }
    }

    override fun getPluginName(): String {
        return "ExcellentShop"
    }
}
