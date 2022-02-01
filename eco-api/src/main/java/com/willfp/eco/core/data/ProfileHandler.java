package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * API to handle profiles.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
public interface ProfileHandler {
    /**
     * Load a player profile.
     *
     * @param uuid The UUID.
     * @return The profile.
     */
    PlayerProfile load(@NotNull UUID uuid);

    /**
     * Load the server profile.
     *
     * @return The profile.
     */
    ServerProfile loadServerProfile();

    /**
     * Unload a player profile from memory.
     * <p>
     * This will not save the profile first.
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
     * @deprecated Saving changes is faster and should be used. Saving a player manually is not recommended.
     */
    @Deprecated
    default void savePlayer(@NotNull UUID uuid) {
        this.saveKeysFor(uuid, PersistentDataKey.values());
    }

    /**
     * Save keys for a player.
     * <p>
     * Can run async if using MySQL.
     *
     * @param uuid The uuid.
     * @param keys The keys.
     */
    void saveKeysFor(@NotNull UUID uuid,
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
     * Commit all changes to the file.
     * <p>
     * Does nothing if using MySQL.
     */
    void save();
}
