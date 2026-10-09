package com.willfp.eco.core.data;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent data storage interface.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface Profile {
    /**
     * Write a key to persistent data.
     * <p>
     * The value is stored in memory immediately and committed to the underlying
     * data handler asynchronously, so this does not block.
     *
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the key.
     */
    <T> void write(@NotNull PersistentDataKey<T> key,
                   @NotNull T value);

    /**
     * Read a key from persistent data.
     * <p>
     * Values are cached in memory once read. The first read of a key may block
     * while the value is fetched from the underlying data handler.
     *
     * @param key The key.
     * @param <T> The type of the key.
     * @return The value, or {@link PersistentDataKey#getDefaultValue()} if not found.
     */
    <T> @NotNull T read(@NotNull PersistentDataKey<T> key);

    /**
     * Atomically replace the value of a key with the result of a function of its current value.
     * <p>
     * Concurrent calls for the same key are serialised, so no update is lost when several
     * threads modify the same key at once. The function may be called more than once and must
     * not access this profile.
     *
     * @param key      The key.
     * @param function The function, given the current value and returning the new value.
     * @param <T>      The type of the key.
     * @return The new value.
     */
    default <T> @NotNull T compute(@NotNull final PersistentDataKey<T> key,
                                   @NotNull final UnaryOperator<T> function) {
        synchronized (this) {
            T value = function.apply(read(key));
            write(key, value);
            return value;
        }
    }

    /**
     * Atomically add to the value of a numeric key.
     *
     * @param key    The key.
     * @param amount The amount to add, which may be negative.
     * @return The new value.
     * @see #compute(PersistentDataKey, UnaryOperator)
     */
    default double add(@NotNull final PersistentDataKey<Double> key,
                       final double amount) {
        return compute(key, value -> value + amount);
    }
}
