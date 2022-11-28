package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.impl.PriceEconomy
import dev.norska.dsw.api.DeluxeSellwandSellEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ShopDeluxeSellwands : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return DeluxeSellwandsSellEventListeners
    }

    object DeluxeSellwandsSellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: DeluxeSellwandSellEvent) {
            if (event.isCancelled) {
                return
            }

            val ecoEvent = ShopSellEvent(event.player, PriceEconomy(event.money), null)
            Bukkit.getPluginManager().callEvent(ecoEvent)
            event.money = ecoEvent.value.getValue(event.player) * ecoEvent.multiplier
        }
    }

    override fun getPluginName(): String {
        return "DeluxeSellwands"
    }
}