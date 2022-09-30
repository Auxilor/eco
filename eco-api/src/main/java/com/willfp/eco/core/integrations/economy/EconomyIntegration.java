package com.willfp.eco.core.integrations.economy;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Wrapper class for economy integrations.
 * <p>
 * If you're adding your economy to be supported in eco,
 * it's recommended to override the {@link BigDecimal} methods
 * as opposed to the {@code double} methods.
 * <p>
 * <strong>You must override at least one of all methods</strong>,
 * i.e. one {@code hasAmount}, one {@code giveMoney}, etc.,
 * otherwise your integration will cause {@link StackOverflowError}.
 * <p>
 * All methods are marked as default to preserve compatibility with
 * integrations made before 6.43.0.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public interface EconomyIntegration extends Integration {
    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount.
     * @return If the player has the amount.
     * @deprecated Use {@link BigDecimal} methods instead.
     */
    @Deprecated(since = "6.43.0")
    default boolean hasAmount(@NotNull OfflinePlayer player,
                              double amount) {
        return hasAmount(player, BigDecimal.valueOf(amount));
    }

    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount
     * @return If the player has the amount.
     */
    default boolean hasAmount(@NotNull OfflinePlayer player,
                              @NotNull BigDecimal amount) {
        return hasAmount(player, amount.doubleValue());
    }

    /**
     * Give money to a player.
     *
     * @param player The player.
     * @param amount The amount to give.
     * @return If the transaction was a success.
     * @deprecated Use {@link BigDecimal} methods instead.
     */
    @Deprecated(since = "6.43.0")
    default boolean giveMoney(@NotNull OfflinePlayer player,
                              double amount) {
        return giveMoney(player, BigDecimal.valueOf(amount));
    }

    /**
     * Give money to a player.
     *
     * @param player The player.
     * @param amount The amount to give.
     * @return If the transaction was a success.
     */
    default boolean giveMoney(@NotNull OfflinePlayer player,
                              @NotNull BigDecimal amount) {
        return giveMoney(player, amount.doubleValue());
    }

    /**
     * Remove money from a player.
     *
     * @param player The player.
     * @param amount The amount to remove.
     * @return If the transaction was a success.
     * @deprecated Use {@link BigDecimal} methods instead.
     */
    @Deprecated(since = "6.43.0")
    default boolean removeMoney(@NotNull OfflinePlayer player,
                                double amount) {
        return removeMoney(player, BigDecimal.valueOf(amount));
    }

    /**
     * Remove money from a player.
     *
     * @param player The player.
     * @param amount The amount to remove.
     * @return If the transaction was a success.
     */
    default boolean removeMoney(@NotNull OfflinePlayer player,
                                @NotNull BigDecimal amount) {
        return removeMoney(player, amount.doubleValue());
    }


    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     * @deprecated Use {@link BigDecimal} methods instead.
     */
    @Deprecated(since = "6.43.0")
    default double getBalance(@NotNull OfflinePlayer player) {
        return getExactBalance(player).doubleValue();
    }

    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     */
    default BigDecimal getExactBalance(@NotNull OfflinePlayer player) {
        return BigDecimal.valueOf(getBalance(player));
    }
}
