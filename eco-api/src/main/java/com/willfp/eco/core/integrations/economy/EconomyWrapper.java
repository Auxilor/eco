package com.willfp.eco.core.integrations.economy;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for economy integrations.
 */
public interface EconomyWrapper extends Integration {
    /**
     * Get if a player has a certain amount.
     *
     * @param player The player.
     * @param amount The amount.
     * @return If the player has the amount.
     */
    boolean hasAmount(@NotNull OfflinePlayer player,
                      double amount);

    /**
     * Give money to a player.
     *
     * @param player The player.
     * @param amount The amount to give.
     * @return If the transaction was a success.
     */
    boolean giveMoney(@NotNull OfflinePlayer player,
                      double amount);

    /**
     * Remove money from a player.
     *
     * @param player The player.
     * @param amount The amount to remove.
     * @return If the transaction was a success.
     */
    boolean removeMoney(@NotNull OfflinePlayer player,
                        double amount);

    /**
     * Get the balance of a player.
     *
     * @param player The player.
     * @return The balance.
     */
    double getBalance(@NotNull OfflinePlayer player);
}
