package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent data storage interface for players.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface PlayerProfile extends Profile {
    /**
     * Load a player profile.
     * <p>
     * Only the player's UUID is used, so this works for offline players.
     *
     * @param player The player.
     * @return The profile.
     */
    @NotNull
    static PlayerProfile load(@NotNull final OfflinePlayer player) {
        return load(Eco.get().getPlayerProfileResolver().resolve(player));
    }

    /**
     * Load a player profile.
     * <p>
     * Loading a profile does not itself read any persistent data; values are only
     * fetched when {@link Profile#read(com.willfp.eco.core.data.keys.PersistentDataKey)}
     * is called.
     *
     * @param uuid The player's UUID.
     * @return The profile.
     */
    @NotNull
    static PlayerProfile load(@NotNull final UUID uuid) {
        return Eco.get().loadPlayerProfile(uuid);
    }
}
