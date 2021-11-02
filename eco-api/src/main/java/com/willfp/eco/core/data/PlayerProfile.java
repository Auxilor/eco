package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Persistent data storage interface for players.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface PlayerProfile {
    /**
     * Write a key to a player's persistent data.
     *
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the key.
     */
    <T> void write(@NotNull PersistentDataKey<T> key,
                   @NotNull T value);

    /**
     * Read a key from a player's persistent data.
     *
     * @param key The key.
     * @param <T> The type of the key.
     * @return The value, or the default value if not found.
     */
    <T> @NotNull T read(@NotNull PersistentDataKey<T> key);

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
        return Eco.getHandler().getPlayerProfileHandler().load(uuid);
    }
}
