package com.willfp.eco.core.integrations.afk;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for afk integrations.
 */
public interface AFKWrapper extends Integration {
    /**
     * Get if a player is afk.
     *
     * @param player The player.
     * @return If afk.
     */
    boolean isAfk(@NotNull final Player player);
}
