package com.willfp.eco.proxy.proxies;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

public interface VillagerTradeProxy extends AbstractProxy {
    /**
     * Display a MerchantRecipe without creating a new one.
     *
     * @param recipe The recipe.
     */
    void displayTrade(@NotNull MerchantRecipe recipe);
}
