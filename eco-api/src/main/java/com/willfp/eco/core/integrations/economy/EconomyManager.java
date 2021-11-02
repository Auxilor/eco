package com.willfp.eco.core.integrations.economy;

import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle economy.
 */
@UtilityClass
public class EconomyManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<EconomyWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final EconomyWrapper integration) {
        registered.add(integration);
    }

    /**
     * Get if any economy registrations are registered.
     *
     * @return If any economy.
     */
    public boolean hasRegistrations() {
        return !registered.isEmpty();
    }

    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount.
     * @return If the player has the amount.
     */
    public boolean hasAmount(@NotNull final OfflinePlayer player,
                             final double amount) {
        for (EconomyWrapper wrapper : registered) {
            return wrapper.hasAmount(player, amount);
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
    public boolean giveMoney(@NotNull final OfflinePlayer player,
                             final double amount) {
        for (EconomyWrapper wrapper : registered) {
            return wrapper.giveMoney(player, amount);
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
    public boolean removeMoney(@NotNull final OfflinePlayer player,
                               final double amount) {
        for (EconomyWrapper wrapper : registered) {
            return wrapper.removeMoney(player, amount);
        }

        return false;
    }

    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     */
    public double getBalance(@NotNull final OfflinePlayer player) {
        for (EconomyWrapper wrapper : registered) {
            return wrapper.getBalance(player);
        }

        return 0;
    }
}
