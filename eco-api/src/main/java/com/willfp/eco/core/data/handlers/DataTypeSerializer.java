package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Handles data read/write for a {@link com.willfp.eco.core.data.keys.PersistentDataKeyType} for a specific
 * data handler.
 *
 * @param <T> The type of data.
 */
public abstract class DataTypeSerializer<T> {
    /**
     * Create a new data type serializer.
     */
    protected DataTypeSerializer() {

    }

    /**
     * Read a value.
     *
     * @param uuid The uuid.
     * @param key  The key.
     * @return The value.
     */
    @Nullable
    public abstract T readAsync(@NotNull final UUID uuid,
                                @NotNull final PersistentDataKey<T> key);

    /**
     * Write a value.
     *
     * @param uuid  The uuid.
     * @param key   The key.
     * @param value The value.
     */
    public abstract void writeAsync(@NotNull final UUID uuid,
                                    @NotNull final PersistentDataKey<T> key,
                                    @NotNull final T value);
}
