package com.willfp.eco.proxy;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

public interface VillagerTradeProxy extends AbstractProxy {
    /**
     * Display a MerchantRecipe.
     *
     * @param recipe The recipe.
     * @param player The player.
     * @return The new recipe.
     */
    MerchantRecipe displayTrade(@NotNull MerchantRecipe recipe,
                                @NotNull Player player);
}
