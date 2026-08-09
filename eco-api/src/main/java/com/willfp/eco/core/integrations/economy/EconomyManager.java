package com.willfp.eco.core.integrations.economy;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import com.willfp.eco.core.registry.Registry;
import java.math.BigDecimal;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle economy.
 * <p>
 * Only one integration is consulted per call: whichever {@link EconomyIntegration} the registry
 * yields first. If none is registered, transactions and balance checks fail safe, returning
 * false and {@link BigDecimal#ZERO} respectively.
 */
public final class EconomyManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<EconomyIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final EconomyIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Unregister an integration by the name of the plugin it hooks into.
     *
     * @param pluginName The plugin name, as passed to {@link EconomyIntegration#getPluginName()}.
     */
    public static void unregister(@NotNull final String pluginName) {
        REGISTRY.remove(Registry.tryFitPattern(pluginName));
    }

    /**
     * Get if any economy integrations are registered.
     *
     * @return If any economy integration is registered.
     */
    public static boolean hasRegistrations() {
        return REGISTRY.isNotEmpty();
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
        return REGISTRY.firstSafely(
                integration -> integration.hasAmount(player, amount),
                false
        );
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
        return REGISTRY.firstSafely(
                integration -> integration.giveMoney(player, amount),
                false
        );
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
        return REGISTRY.firstSafely(
                integration -> integration.removeMoney(player, amount),
                false
        );
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
        return REGISTRY.firstSafely(
                integration -> integration.getExactBalance(player),
                BigDecimal.ZERO
        );
    }

    private EconomyManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
