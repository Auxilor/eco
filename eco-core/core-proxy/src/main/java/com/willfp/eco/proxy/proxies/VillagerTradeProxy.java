package com.willfp.eco.proxy.proxies;

import com.willfp.eco.util.proxy.AbstractProxy;
import org.bukkit.inventory.MerchantRecipe;

public interface VillagerTradeProxy extends AbstractProxy {
    /**
     * Apply enchant display to the result of trades.
     *
     * @param merchantRecipe The recipe to modify.
     */
    void displayTrade(MerchantRecipe merchantRecipe);
}
