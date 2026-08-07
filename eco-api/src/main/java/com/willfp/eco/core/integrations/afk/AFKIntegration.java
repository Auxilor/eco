package com.willfp.eco.core.integrations.afk;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper interface for AFK integrations.
 * <p>
 * Implemented for plugins that track player AFK state, such as Essentials and CMI,
 * so that eco can ask whether a player is currently AFK without depending on them directly.
 *
 * @see AFKManager
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
