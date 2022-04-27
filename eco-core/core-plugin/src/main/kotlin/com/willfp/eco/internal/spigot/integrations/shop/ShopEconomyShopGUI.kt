package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import me.gypopo.economyshopgui.api.events.PreTransactionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ShopEconomyShopGUI : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return EconomyShopGUISellEventListeners
    }

    object EconomyShopGUISellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: PreTransactionEvent) {
            if (event.isCancelled) {
                return
            }

            val ecoEvent = ShopSellEvent(event.player, event.price, event.itemStack)
            Bukkit.getPluginManager().callEvent(ecoEvent)
            event.price = ecoEvent.price
        }
    }

    override fun getPluginName(): String {
        return "EconomyShopGUI"
    }
}