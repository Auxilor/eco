package com.willfp.eco.core.integrations.afk;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle afk integrations.
 */
public final class AFKManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<AFKIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final AFKIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Get if a player is afk.
     *
     * @param player The player.
     * @return If afk.
     */
    public static boolean isAfk(@NotNull final Player player) {
        return REGISTRY.anySafely(integration -> integration.isAfk(player));
    }

    private AFKManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
