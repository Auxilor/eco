package com.willfp.eco.core.integrations.guidetection;

import com.willfp.eco.util.MenuUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle GUI detection.
 */
public final class GUIDetectionManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<GUIDetectionIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final GUIDetectionIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Get if a player is in a GUI.
     *
     * @param player The player.
     * @return If the player has a GUI open.
     */
    public static boolean hasGUIOpen(@NotNull final Player player) {
        if (MenuUtils.getOpenMenu(player) != null) {
            return true;
        }

        for (GUIDetectionIntegration integration : REGISTERED) {
            if (integration.hasGUIOpen(player)) {
                return true;
            }
        }

        return false;
    }

    private GUIDetectionManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
