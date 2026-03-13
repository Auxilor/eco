package com.willfp.eco.core.price;

import kotlin.NotImplementedError;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A price that a player should pay.
 * <p>
 * There are important implementation details:
 * <p>
 * For backwards compatibility, all methods are default, however you must override the following:
 * <ul>
 *     <li><code>canAfford(Player, double)</code></li>
 *     <li><code>pay(Player, double)</code></li>
 *     <li><code>giveTo(Player, double)</code></li>
 *     <li><code>getValue(Player, double)</code></li>
 *     <li><code>getMultiplier(Player)</code></li>
 *     <li><code>setMultiplier(Player, double)</code></li>
 * </ul>
 * Otherwise, your implementation will throw {@link NotImplementedError}.
 * <p>
 * Also, getValue() should always return the value with player multipliers applied.
 */
public interface Price {
    /**
     * Get if a player can afford to pay the price.
     *
     * @param player The player.
     * @return If the player can afford.
     */
    default boolean canAfford(@NotNull final Player player) {
        return this.canAfford(player, 1);
    }

    /**
     * Get if a player can afford to pay x times the price.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     * @return If the player can afford.
     */
    default boolean canAfford(@NotNull final Player player,
                              final double multiplier) {
        throw new NotImplementedError("Override canAfford(Player, double) in your Price implementation!");
    }

    /**
     * Make the player pay the price.
     * <p>
     * Check canAfford first.
     *
     * @param player The player.
     */
    default void pay(@NotNull final Player player) {
        this.pay(player, 1);
    }

    /**
     * Make the player pay the price x times.
     * <p>
     * Check canAfford first.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     */
    default void pay(@NotNull final Player player,
                     final double multiplier) {
        throw new NotImplementedError("Override pay(Player, double) in your Price implementation!");
    }

    /**
     * Give the price to the player.
     *
     * @param player The player.
     */
    default void giveTo(@NotNull final Player player) {
        this.giveTo(player, 1);
    }

    /**
     * Give the price to the player x times.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     */
    default void giveTo(@NotNull final Player player,
                        final double multiplier) {
        throw new NotImplementedError("Override giveTo(Player, double) in your Price implementation!");
    }

    /**
     * Get the numerical value that backs this price.
     *
     * @param player The player.
     * @return The value.
     */
    default double getValue(@NotNull final Player player) {
        return getValue(player, 1);
    }

    /**
     * Get the numeral value that backs this price multiplied x times.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     * @return The value.
     */
    default double getValue(@NotNull final Player player,
                            final double multiplier) {
        throw new NotImplementedError("Override getValue(Player, double) in your Price implementation!");
    }

    /**
     * Get the value multiplier for the player.
     *
     * @param player The player.
     * @return The multiplier.
     */
    default double getMultiplier(@NotNull final Player player) {
        return 1;
    }

    /**
     * Set the value multiplier for the player.
     *
     * @param player     The player.
     * @param multiplier The multiplier.
     */
    default void setMultiplier(@NotNull final Player player,
                               final double multiplier) {
        throw new NotImplementedError("Override setMultiplier(Player, double) in your Price implementation!");
    }

    /**
     * Get the identifier of this price (as type/instance checks break with delegation,
     * this is used for combining prices, etc.)
     * <p>
     * By default, this uses the class name, but it's good practice to override this.
     * <p>
     * It's also good practice to prefix your identifiers with some kind of namespace or
     * internal ID, in order to prevent conflicts.
     *
     * @return The identifier.
     */
    default String getIdentifier() {
        return this.getClass().getName();
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
     * @deprecated Values shouldn't be fixed. This method should never work.
     */
    @Deprecated(since = "6.45.0", forRemoval = true)
    default void setValue(final double value) {
        // Override when needed.
    }
}
