package com.willfp.eco.core.data;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Resolves the UUID that a player's persistent data is stored against.
 * <p>
 * Only {@link PlayerProfile#load(OfflinePlayer)} uses the resolver, so
 * {@link PlayerProfile#load(UUID)} can still be used to reach a player's own data.
 */
@FunctionalInterface
public interface PlayerProfileResolver {
    /**
     * Resolve the UUID for a player.
     *
     * @param player The player.
     * @return The UUID.
     */
    @NotNull
    UUID resolve(@NotNull OfflinePlayer player);
}
