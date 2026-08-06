package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles data read/write for a {@link com.willfp.eco.core.data.keys.PersistentDataKeyType} for a specific
 * data handler.
 * <p>
 * Both methods are always invoked off the main thread by
 * {@link PersistentDataHandler}, so they may block.
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
     * Read a value, on the data handler's executor.
     *
     * @param uuid The uuid of the profile to read from.
     * @param key  The key.
     * @return The value, or null if no value is stored for the key.
     */
    @Nullable
    public abstract T readAsync(@NotNull final UUID uuid,
                                @NotNull final PersistentDataKey<T> key);

    /**
     * Write a value, on the data handler's executor.
     *
     * @param uuid  The uuid of the profile to write to.
     * @param key   The key.
     * @param value The value.
     */
    public abstract void writeAsync(@NotNull final UUID uuid,
                                    @NotNull final PersistentDataKey<T> key,
                                    @NotNull final T value);
}
