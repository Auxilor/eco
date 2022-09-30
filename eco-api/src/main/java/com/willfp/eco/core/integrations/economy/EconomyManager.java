package com.willfp.eco.core.integrations.economy;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle economy.
 */
public final class EconomyManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<EconomyIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final EconomyIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Get if any economy registrations are registered.
     *
     * @return If any economy.
     */
    public static boolean hasRegistrations() {
        return !REGISTERED.isEmpty();
    }

    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount.
     * @return If the player has the amount.
     */
    public static boolean hasAmount(@NotNull final OfflinePlayer player,
                                    final double amount) {
        return hasAmount(player, BigDecimal.valueOf(amount));
    }

    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount.
     * @return If the player has the amount.
     */
    public static boolean hasAmount(@NotNull final OfflinePlayer player,
                                    final BigDecimal amount) {
        for (EconomyIntegration integration : REGISTERED) {
            return integration.hasAmount(player, amount);
        }

        return false;
    }

    /**
     * Give money to a player.
     *
     * @param player The player.
     * @param amount The amount to give.
     * @return If the transaction was a success.
     */
    public static boolean giveMoney(@NotNull final OfflinePlayer player,
                                    final double amount) {
        return giveMoney(player, BigDecimal.valueOf(amount));
    }

    /**
     * Give money to a player.
     *
     * @param player The player.
     * @param amount The amount to give.
     * @return If the transaction was a success.
     */
    public static boolean giveMoney(@NotNull final OfflinePlayer player,
                                    @NotNull final BigDecimal amount) {
        for (EconomyIntegration integration : REGISTERED) {
            return integration.giveMoney(player, amount);
        }

        return false;
    }

    /**
     * Remove money from a player.
     *
     * @param player The player.
     * @param amount The amount to remove.
     * @return If the transaction was a success.
     */
    public static boolean removeMoney(@NotNull final OfflinePlayer player,
                                      final double amount) {
        return removeMoney(player, BigDecimal.valueOf(amount));
    }

    /**
     * Remove money from a player.
     *
     * @param player The player.
     * @param amount The amount to remove.
     * @return If the transaction was a success.
     */
    public static boolean removeMoney(@NotNull final OfflinePlayer player,
                                      @NotNull final BigDecimal amount) {
        for (EconomyIntegration integration : REGISTERED) {
            return integration.removeMoney(player, amount);
        }

        return false;
    }

    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     */
    public static double getBalance(@NotNull final OfflinePlayer player) {
        return getExactBalance(player).doubleValue();
    }

    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     */
    public static BigDecimal getExactBalance(@NotNull final OfflinePlayer player) {
        for (EconomyIntegration integration : REGISTERED) {
            return integration.getExactBalance(player);
        }

        return BigDecimal.ZERO;
    }

    private EconomyManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
