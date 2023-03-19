package com.willfp.eco.core.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * A simple store key-value store for data to be stored outside of plugins.
 */
public final class ExternalDataStore {
    /**
     * The store.
     */
    private static final HashMap<String, Object> data = new HashMap<>();

    /**
     * Put data into the store.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void put(@NotNull final String key,
                           @NotNull final Object value) {
        data.put(key, value);
    }

    /**
     * Get data from the store.
     *
     * @param key   The key.
     * @param clazz The class.
     * @param <T>   The type.
     * @return The value.
     */
    @Nullable
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz) {
        Object value = data.get(key);

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            return null;
        }
    }

    /**
     * Get data from the store.
     *
     * @param key          The key.
     * @param clazz        The class.
     * @param defaultValue The default value.
     * @param <T>          The type.
     * @return The value.
     */
    @NotNull
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz,
                            @NotNull final T defaultValue) {
        T value = get(key, clazz);
        return value == null ? defaultValue : value;
    }

    /**
     * Get data from the store.
     *
     * @param key          The key.
     * @param clazz        The class.
     * @param defaultValue The default value.
     * @param <T>          The type.
     * @return The value.
     */
    @NotNull
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz,
                            @NotNull final Supplier<T> defaultValue) {
        return get(key, clazz, defaultValue.get());
    }

    private ExternalDataStore() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
