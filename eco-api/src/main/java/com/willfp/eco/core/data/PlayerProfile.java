package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Persistent data storage interface for players.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface PlayerProfile extends Profile {
    /**
     * Load a player profile.
     *
     * @param player The player.
     * @return The profile.
     */
    @NotNull
    static PlayerProfile load(@NotNull final OfflinePlayer player) {
        return load(player.getUniqueId());
    }

    /**
     * Load a player profile.
     *
     * @param uuid The player's UUID.
     * @return The profile.
     */
    @NotNull
    static PlayerProfile load(@NotNull final UUID uuid) {
        return Eco.get().loadPlayerProfile(uuid);
    }
}
