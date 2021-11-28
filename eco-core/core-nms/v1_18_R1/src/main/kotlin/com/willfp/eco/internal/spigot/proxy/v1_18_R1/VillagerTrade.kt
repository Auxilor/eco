package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.VillagerTradeProxy
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

class VillagerTrade : VillagerTradeProxy {
    override fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe {
        return recipe
    }
}
