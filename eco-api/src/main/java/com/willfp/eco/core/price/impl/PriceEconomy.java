package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.integrations.economy.EconomyManager;
import com.willfp.eco.core.math.MathContext;
import com.willfp.eco.core.price.Price;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Economy-based price (for Vault, Treasury, etc.)
 */
public final class PriceEconomy implements Price {
    /**
     * The value of the price.
     */
    private final Function<MathContext, Double> function;

    /**
     * The base math context.
     */
    private final MathContext baseContext;

    /**
     * The multipliers.
     */
    private final Map<UUID, Double> multipliers = new HashMap<>();

    /**
     * Create a new economy-based price.
     *
     * @param value The value.
     */
    public PriceEconomy(final double value) {
        this(MathContext.EMPTY, ctx -> value);
    }

    /**
     * Create a new economy-based price.
     *
     * @param baseContext The base context.
     * @param function    The function.
     */
    public PriceEconomy(@NotNull final MathContext baseContext,
                        @NotNull final Function<MathContext, Double> function) {
        this.baseContext = baseContext;
        this.function = function;
    }

    @Override
    public boolean canAfford(@NotNull final Player player,
                             final double multiplier) {
        return EconomyManager.getBalance(player) >= getValue(player, multiplier);
    }

    @Override
    public void pay(@NotNull final Player player,
                    final double multiplier) {
        EconomyManager.removeMoney(player, getValue(player, multiplier));
    }

    @Override
    public void giveTo(@NotNull final Player player,
                       final double multiplier) {
        EconomyManager.giveMoney(player, getValue(player, multiplier));
    }

    @Override
    public double getValue(@NotNull final Player player,
                           final double multiplier) {
        return this.function.apply(MathContext.copyWithPlayer(baseContext, player)) * getMultiplier(player) * multiplier;
    }

    @Override
    public double getMultiplier(@NotNull final Player player) {
        return this.multipliers.getOrDefault(player.getUniqueId(), 1.0);
    }

    @Override
    public void setMultiplier(@NotNull final Player player,
                              final double multiplier) {
        this.multipliers.put(player.getUniqueId(), multiplier);
    }

    @Override
    public String getIdentifier() {
        return "eco:economy";
    }
}
