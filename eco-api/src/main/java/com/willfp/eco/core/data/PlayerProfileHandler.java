package com.willfp.eco.core.data;

import org.jetbrains.annotations.NotNull;

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
     * Save a player profile.
     *
     * @param uuid The uuid.
     */
    void savePlayer(@NotNull UUID uuid);

    /**
     * Save all player data.
     *
     * @param async If the saving should be done asynchronously.
     */
    void saveAll(boolean async);
}
