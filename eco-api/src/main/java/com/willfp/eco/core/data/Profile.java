package com.willfp.eco.core.data;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent data storage interface.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface Profile {
    /**
     * Write a key to persistent data.
     *
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the key.
     */
    <T> void write(@NotNull PersistentDataKey<T> key,
                   @NotNull T value);

    /**
     * Read a key from persistent data.
     *
     * @param key The key.
     * @param <T> The type of the key.
     * @return The value, or the default value if not found.
     */
    <T> @NotNull T read(@NotNull PersistentDataKey<T> key);
}
