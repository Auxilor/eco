package com.willfp.eco.core.integrations.guidetection;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for GUI integrations.
 */
public interface GUIDetectionIntegration extends Integration {
    /**
     * Determine if a player is in a GUI.
     *
     * @param player The player.
     * @return If the player is in a GUI.
     */
    boolean hasGUIOpen(@NotNull final Player player);
}
