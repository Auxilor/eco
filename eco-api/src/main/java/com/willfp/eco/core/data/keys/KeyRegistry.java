package com.willfp.eco.core.data.keys;

import com.willfp.eco.core.Eco;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * API to register persistent data keys.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
public interface KeyRegistry {
    /**
     * Register a persistent data key to be stored.
     *
     * @param key The key.
     */
    void registerKey(@NotNull PersistentDataKey<?> key);

    /**
     * Get all registered keys.
     *
     * @return The keys.
     */
    Set<PersistentDataKey<?>> getRegisteredKeys();

    /**
     * Mark key as category.
     *
     * @param key      The key.
     * @param category The category.
     */
    void markKeyAs(@NotNull PersistentDataKey<?> key,
                   @NotNull KeyRegistry.KeyCategory category);

    /**
     * Get persistent data key from namespaced key.
     *
     * @param namespacedKey The key.
     * @return The key, or null if not found.
     */
    @Nullable
    PersistentDataKey<?> getKeyFrom(@NotNull NamespacedKey namespacedKey);

    /**
     * Locations for key categorization.
     */
    enum KeyCategory {
        /**
         * Player keys.
         */
        PLAYER,

        /**
         * Server keys.
         */
        SERVER
    }
}
