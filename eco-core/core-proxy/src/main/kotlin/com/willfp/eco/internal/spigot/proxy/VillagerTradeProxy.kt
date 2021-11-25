package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.proxy.AbstractProxy
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

interface VillagerTradeProxy : AbstractProxy {
    fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe
}