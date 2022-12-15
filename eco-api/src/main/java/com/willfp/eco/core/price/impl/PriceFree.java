package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.price.Price;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Free (default) price.
 */
public final class PriceFree implements Price {
    /**
     * Create a new free price.
     */
    public PriceFree() {

    }

    @Override
    public boolean canAfford(@NotNull final Player player,
                             final double multiplier) {
        return true;
    }

    @Override
    public void pay(@NotNull final Player player,
                    final double multiplier) {
        // Nothing.
    }

    @Override
    public void giveTo(@NotNull final Player player,
                       final double multiplier) {
        // Nothing.
    }

    @Override
    public double getMultiplier(@NotNull final Player player) {
        return 1.0;
    }

    @Override
    public void setMultiplier(@NotNull final Player player,
                              final double multiplier) {
        // Nothing.
    }

    @Override
    public double getValue(@NotNull final Player player,
                           final double multiplier) {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return "eco:free";
    }
}
