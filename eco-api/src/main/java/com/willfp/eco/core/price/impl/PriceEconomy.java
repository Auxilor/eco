package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.integrations.economy.EconomyManager;
import com.willfp.eco.core.price.Price;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Economy-based price (for Vault, Treasury, etc.)
 */
public final class PriceEconomy implements Price {
    /**
     * The value of the price.
     */
    private final double value;

    /**
     * Create a new economy-based price.
     *
     * @param value The value.
     */
    public PriceEconomy(final double value) {
        this.value = value;
    }

    @Override
    public boolean canAfford(@NotNull Player player) {
        return EconomyManager.getBalance(player) >= value;
    }

    @Override
    public void pay(@NotNull Player player) {
        EconomyManager.removeMoney(player, value);
    }
}
