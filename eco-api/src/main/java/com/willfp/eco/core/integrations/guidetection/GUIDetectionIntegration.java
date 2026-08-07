package com.willfp.eco.core.integrations.guidetection;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper interface for GUI detection integrations.
 * <p>
 * Implemented for plugins that open their own inventory menus, such as DeluxeMenus, so that eco
 * can tell whether a player is currently looking at a menu rather than at the world.
 *
 * @see GUIDetectionManager
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
