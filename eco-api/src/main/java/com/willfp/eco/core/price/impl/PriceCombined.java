package com.willfp.eco.core.price.impl;

import com.willfp.eco.core.price.Price;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Several prices charged together.
 * <p>
 * The value is the value of the first price, so ratios between values stay correct.
 */
public final class PriceCombined implements Price {
    /**
     * The prices.
     */
    private final List<Price> prices;

    /**
     * Create a new combined price.
     *
     * @param prices The prices, at least one.
     */
    public PriceCombined(@NotNull final List<? extends Price> prices) {
        if (prices.isEmpty()) {
            throw new IllegalArgumentException("A combined price needs at least one price");
        }

        this.prices = List.copyOf(prices);
    }

    @Override
    public boolean canAfford(@NotNull final Player player,
                             final double multiplier) {
        return prices.stream().allMatch(price -> price.canAfford(player, multiplier));
    }

    @Override
    public void pay(@NotNull final Player player,
                    final double multiplier) {
        prices.forEach(price -> price.pay(player, multiplier));
    }

    @Override
    public void giveTo(@NotNull final Player player,
                       final double multiplier) {
        prices.forEach(price -> price.giveTo(player, multiplier));
    }

    @Override
    public double getValue(@NotNull final Player player,
                           final double multiplier) {
        return prices.getFirst().getValue(player, multiplier);
    }

    @Override
    public double getMultiplier(@NotNull final Player player) {
        return prices.getFirst().getMultiplier(player);
    }

    @Override
    public void setMultiplier(@NotNull final Player player,
                              final double multiplier) {
        prices.forEach(price -> price.setMultiplier(player, multiplier));
    }

    @Override
    public String getIdentifier() {
        return "eco:combined:" + prices.stream()
                .map(Price::getIdentifier)
                .collect(Collectors.joining(","));
    }

    /**
     * Get the prices.
     *
     * @return The prices.
     */
    @NotNull
    public List<Price> getPrices() {
        return prices;
    }
}
