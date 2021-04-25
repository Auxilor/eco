package com.willfp.eco.proxy.proxies;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

public interface VillagerTradeProxy extends AbstractProxy {
    /**
     * Display a MerchantRecipe.
     *
     * @param recipe The recipe.
     * @return The new recipe.
     */
    MerchantRecipe displayTrade(@NotNull MerchantRecipe recipe);
}
