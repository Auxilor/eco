package com.willfp.eco.core.price;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A price that a player should pay.
 */
public interface Price {
    /**
     * Get if the player can afford the price.
     *
     * @param player The player.
     * @return If the player can afford.
     */
    boolean canAfford(@NotNull Player player);

    /**
     * Make the player pay the price.
     * <p>
     * Only run this if the player can afford the price.
     *
     * @param player The player.
     */
    void pay(@NotNull Player player);

    /**
     * Give the value of the price to the player.
     * <p>
     * You should override this method, it's only marked as default for
     * backwards compatibility purposes.
     *
     * @param player The player.
     */
    default void giveTo(@NotNull Player player) {
        // Override when needed.
    }

    /**
     * If the price is backed by a value, get it here.
     *
     * @param player The player.
     * @return The value.
     */
    default double getValue(@NotNull final Player player) {
        return 0;
    }

    /**
     * If the price is backed by a value, get it here.
     *
     * @return The value.
     * @deprecated Use getValue(Player) instead.
     */
    @Deprecated(since = "6.45.0", forRemoval = true)
    default double getValue() {
        return 0;
    }

    /**
     * If the price is backed by a value, set it here.
     *
     * @param value The value.
     * @deprecated Values shouldn't be fixed.
     */
    @Deprecated(since = "6.45.0", forRemoval = true)
    default void setValue(final double value) {
        // Override when needed.
    }

    /**
     * Get the price multiplier for a player.
     *
     * @param player The player.
     * @return The value.
     */
    default double getMultiplier(@NotNull final Player player) {
        return 1;
    }

    /**
     * Set the price multiplier for a player.
     *
     * @param player     The        player.
     * @param multiplier The multiplier.
     */
    default void setMultiplier(@NotNull final Player player,
                               final double multiplier) {
        // Override when needed.
    }

    /**
     * Copy this price with a given base multiplier.
     *
     * @param multiplier The multiplier.
     */
    @NotNull
    default Price withMultiplier(final double multiplier) {
        return this;
    }
}
