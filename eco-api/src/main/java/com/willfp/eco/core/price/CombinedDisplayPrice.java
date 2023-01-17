package com.willfp.eco.core.price;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A group of {@link ConfiguredPrice}s in order to show them
 * to players in one go.
 */
public final class CombinedDisplayPrice {
    /**
     * Maps configured prices to multipliers.
     */
    private final Map<ConfiguredPrice, Double> prices;

    /**
     * The player to format for.
     */
    private final Player player;

    /**
     * Initialize a new combined price mapping formatters to multipliers.
     *
     * @param player The player.
     * @param prices The prices.
     */
    private CombinedDisplayPrice(@NotNull final Player player,
                                 @NotNull final Map<ConfiguredPrice, Double> prices) {
        this.player = player;
        this.prices = prices;
    }

    /**
     * Get the display strings.
     *
     * @return The display strings.
     */
    @NotNull
    public String[] getDisplayStrings() {
        List<String> displayStrings = new ArrayList<>();

        for (Map.Entry<ConfiguredPrice, Double> entry : prices.entrySet()) {
            displayStrings.add(entry.getKey().getDisplay(player, entry.getValue()));
        }

        return displayStrings.toArray(new String[0]);
    }

    /**
     * The builder.
     */
    public static class Builder {
        /**
         * All multiplied prices.
         */
        private final List<MultipliedPrice> prices = new ArrayList<>();

        /**
         * The player.
         */
        private final Player player;

        /**
         * Create a new builder.
         *
         * @param player The player.
         */
        Builder(@NotNull final Player player) {
            this.player = player;
        }

        /**
         * Add a new price with a certain multiplier.
         *
         * @param price      The price.
         * @param multiplier The multiplier.
         * @return The builder.
         */
        @NotNull
        public Builder add(@NotNull final ConfiguredPrice price,
                           final double multiplier) {
            prices.add(new MultipliedPrice(price, multiplier));
            return this;
        }


        /**
         * Add a new price.
         *
         * @param price The price.
         * @return The builder.
         */
        @NotNull
        public Builder add(@NotNull final ConfiguredPrice price) {
            return this.add(price, 1D);
        }

        /**
         * Build into a {@link CombinedDisplayPrice}.
         *
         * @return The combined price.
         */
        @NotNull
        public CombinedDisplayPrice build() {
            Map<ConfiguredPrice, Double> unitPrices = new HashMap<>();

            // Take first configured price at each ID as the format for all prices with that ID.
            for (MultipliedPrice price : prices) {
                // Find the base price.
                ConfiguredPrice base = unitPrices.keySet()
                        .stream()
                        .filter(it -> it.getIdentifier().equals(price.price().getIdentifier()))
                        .findFirst()
                        .orElse(price.price());

                // Find the multiplier for a value of 1, e.g. a price that's worth 20 will be 0.05.
                double unitMultiplier = 1 / base.getValue(player);

                double currentMultiplier = unitPrices.getOrDefault(base, 0D);
                currentMultiplier += unitMultiplier * price.price().getValue(player, price.multiplier());
                unitPrices.put(base, currentMultiplier);
            }

            return new CombinedDisplayPrice(player, unitPrices);
        }

        /**
         * A price with a multiplier.
         *
         * @param price      The price.
         * @param multiplier The multiplier.
         */
        private record MultipliedPrice(
                @NotNull ConfiguredPrice price,
                double multiplier
        ) {

        }
    }

    /**
     * Create a new builder for a player.
     *
     * @param player The player.
     * @return The builder.
     */
    @NotNull
    public static Builder builder(@NotNull final Player player) {
        return new Builder(player);
    }
}
