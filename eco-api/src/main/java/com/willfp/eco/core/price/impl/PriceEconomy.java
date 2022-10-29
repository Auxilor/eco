package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.integrations.economy.EconomyManager;
import com.willfp.eco.core.price.Price;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Economy-based price (for Vault, Treasury, etc.)
 */
public final class PriceEconomy implements Price {
    /**
     * The value of the price.
     */
    private final Supplier<Double> function;

    private double multiplier;

    /**
     * Create a new economy-based price.
     *
     * @param value The value.
     */
    public PriceEconomy(final double value) {
        this.function = () -> value;
    }

    /**
     * Create a new economy-based price.
     *
     * @param function The function.
     */
    public PriceEconomy(@NotNull final Supplier<@NotNull Double> function) {
        this.function = function;
    }

    @Override
    public boolean canAfford(@NotNull final Player player) {
        return EconomyManager.getBalance(player) >= getValue();
    }

    @Override
    public void pay(@NotNull final Player player) {
        EconomyManager.removeMoney(player, getValue());
    }

    @Override
    public void giveTo(@NotNull final Player player) {
        EconomyManager.giveMoney(player, getValue());
    }

    @Override
    public double getValue() {
        return function.get() * multiplier;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(final double multiplier) {
        this.multiplier = multiplier;
    }
}
