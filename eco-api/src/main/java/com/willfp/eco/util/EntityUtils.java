package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for entities.
 */
public final class EntityUtils {
    /**
     * Set a client-side entity display name.
     *
     * @param entity  The entity.
     * @param player  The player.
     * @param name    The display name.
     * @param visible If the display name should be forcibly visible.
     */
    public static void setClientsideDisplayName(@NotNull final LivingEntity entity,
                                                @NotNull final Player player,
                                                @NotNull final Component name,
                                                final boolean visible) {
        Eco.get().setClientsideDisplayName(entity, player, name, visible);
    }

    private EntityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
