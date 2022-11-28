package com.willfp.eco.internal.spigot.integrations.shop

import com.willfp.eco.core.integrations.shop.ShopIntegration
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.price.impl.PriceEconomy
import fr.maxlego08.shop.api.events.ZShopSellEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ShopZShop : ShopIntegration {
    override fun getSellEventAdapter(): Listener {
        return ZShopSellEventListeners
    }

    object ZShopSellEventListeners : Listener {
        @EventHandler
        fun shopEventToEcoEvent(event: ZShopSellEvent) {
            if (event.isCancelled) {
                return
            }

            val ecoEvent = ShopSellEvent(event.player, PriceEconomy(event.price), event.button.itemStack)
            Bukkit.getPluginManager().callEvent(ecoEvent)
            event.price = ecoEvent.value.getValue(event.player) * ecoEvent.multiplier
        }
    }

    override fun getPluginName(): String {
        return "zShop"
    }
}
