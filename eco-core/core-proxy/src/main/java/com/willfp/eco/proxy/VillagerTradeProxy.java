package com.willfp.eco.proxy;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

public interface VillagerTradeProxy extends AbstractProxy {
    MerchantRecipe displayTrade(@NotNull MerchantRecipe recipe,
                                @NotNull Player player);
}
