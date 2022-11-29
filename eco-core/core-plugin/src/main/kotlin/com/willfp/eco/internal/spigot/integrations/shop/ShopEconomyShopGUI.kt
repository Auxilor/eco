package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.impl.PriceEconomy
import me.gypopo.economyshopgui.api.EconomyShopGUIHook
import me.gypopo.economyshopgui.api.events.PreTransactionEvent
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
        return PriceEconomy(
            EconomyShopGUIHook.getItemSellPrice(player, itemStack.clone().apply {
                amount = 1
            })
        )
    }

    override fun isSellable(itemStack: ItemStack, player: Player): Boolean {
        return EconomyShopGUIHook.getItemSellPrice(player, itemStack) > 0
    }

    object EconomyShopGUISellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: PreTransactionEvent) {
            if (event.isCancelled) {
                return
            }

            val ecoEvent = ShopSellEvent(event.player, PriceEconomy(event.price), event.itemStack)
            Bukkit.getPluginManager().callEvent(ecoEvent)
            event.price = ecoEvent.value.getValue(event.player) * ecoEvent.multiplier
        }
    }

    override fun getPluginName(): String {
        return "EconomyShopGUI"
    }
}