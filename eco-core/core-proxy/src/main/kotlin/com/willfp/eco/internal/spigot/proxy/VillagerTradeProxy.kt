package com.willfp.eco.internal.spigot.proxy

import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

interface VillagerTradeProxy {
    fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe
}