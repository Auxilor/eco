package com.willfp.eco.core.data.keys;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * API to register persistent data keys.
 */
public interface KeyRegistry {
    /**
     * Register a persistent data key to be stored.
     *
     * @param key The key.
     */
    void registerKey(@NotNull final PersistentDataKey<?> key);

    /**
     * Get all registered keys.
     *
     * @return The keys.
     */
    Set<PersistentDataKey<?>> getRegisteredKeys();
}
