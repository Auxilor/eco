package com.willfp.eco.core.integrations.afk;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * AFK Integration.
 */
public interface AFKIntegration extends Integration {
    /**
     * Get if a player is afk.
     *
     * @param player The player.
     * @return If afk.
     */
    boolean isAfk(@NotNull Player player);
}
