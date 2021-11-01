package com.willfp.eco.core.integrations.afk;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle afk integrations.
 */
@UtilityClass
public class AFKManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<AFKWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final AFKWrapper integration) {
        registered.add(integration);
    }

    /**
     * Get if a player is afk.
     *
     * @param player The player.
     * @return If afk.
     */
    public boolean isAfk(@NotNull final Player player) {
        for (AFKWrapper afkWrapper : registered) {
            if (afkWrapper.isAfk(player)) {
                return true;
            }
        }

        return false;
    }
}
