package com.willfp.eco.core.data;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * API to handle player profiles.
 */
public interface PlayerProfileHandler {
    /**
     * Load a player profile.
     *
     * @param uuid The UUID.
     * @return The profile.
     */
    PlayerProfile load(@NotNull UUID uuid);

    /**
     * Unload a player profile from memory.
     * <p>
     * This will not save the profile first, so to avoid data loss, run a blocking
     * save beforehand.
     *
     * @param uuid The uuid.
     */
    void unloadPlayer(@NotNull UUID uuid);

    /**
     * Save a player profile.
     * <p>
     * Can run async if using MySQL.
     *
     * @param uuid The uuid.
     */
    default void savePlayer(@NotNull UUID uuid) {
        this.saveKeysForPlayer(uuid, PersistentDataKey.values());
    }

    /**
     * Save keys for a player.
     * <p>
     * Can run async if using MySQL.
     *
     * @param uuid The uuid.
     * @param keys The keys.
     */
    void saveKeysForPlayer(@NotNull UUID uuid,
                           @NotNull Set<PersistentDataKey<?>> keys);

    /**
     * Save all player data.
     *
     * @param async If the saving should be done asynchronously.
     * @deprecated async is now handled automatically depending on implementation.
     */
    @Deprecated
    default void saveAll(boolean async) {
        saveAll();
    }

    /**
     * Save all player data.
     * <p>
     * Can run async if using MySQL.
     */
    void saveAll();

    /**
     * Save all player data, forcibly synchronously.
     */
    void saveAllBlocking();
}
