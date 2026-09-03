package com.willfp.eco.core.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple key-value store for data to be stored outside of plugins.
 * <p>
 * The store is static and shared by every plugin, and is not persisted between restarts.
 */
@SuppressWarnings("unchecked")
public final class ExternalDataStore {
    /**
     * The store.
     */
    private static final Map<String, Object> DATA = new ConcurrentHashMap<>();

    /**
     * The store adapters.
     */
    private static final List<ExternalDataStoreObjectAdapter<?, ?>> STORE_ADAPTERS = new CopyOnWriteArrayList<>();

    /**
     * Put data into the store.
     * <p>
     * If a registered {@link ExternalDataStoreObjectAdapter} accepts the value, the value
     * is converted to its stored form first.
     *
     * @param key   The key.
     * @param value The value.
     */
    public static void put(@NotNull final String key,
                           @NotNull final Object value) {
        doPut(key, value);
    }

    /**
     * Put data into the store.
     *
     * @param key   The key.
     * @param value The value.
     * @param <A>   The stored type.
     */
    private static <A> void doPut(@NotNull final String key,
                                  @NotNull final A value) {
        Object storedValue = value;

        for (ExternalDataStoreObjectAdapter<?, ?> unknownAdapter : STORE_ADAPTERS) {
            if (unknownAdapter.getAccessedClass().isInstance(value)) {
                ExternalDataStoreObjectAdapter<A, ?> adapter = (ExternalDataStoreObjectAdapter<A, ?>) unknownAdapter;
                storedValue = adapter.toStoredObject(value);
                break;
            }
        }

        DATA.put(key, storedValue);
    }

    /**
     * Get data from the store.
     *
     * @param key   The key.
     * @param clazz The class.
     * @param <T>   The type.
     * @return The value, or null if there is nothing stored under the key or the
     * stored value is not of the given type.
     */
    @Nullable
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz) {
        return doGet(key, clazz);
    }

    /**
     * Get data from the store.
     *
     * @param key   The key.
     * @param clazz The class.
     * @param <A>   The accessed type.
     * @param <S>   The stored type.
     * @return The value, or null if there is nothing stored under the key or the
     * stored value is not of the given type.
     */
    @Nullable
    private static <A, S> A doGet(@NotNull final String key,
                                  @NotNull final Class<A> clazz) {
        Object value = DATA.get(key);

        for (ExternalDataStoreObjectAdapter<?, ?> unknownAdapter : STORE_ADAPTERS) {
            if (unknownAdapter.getStoredClass().isInstance(value) && unknownAdapter.getAccessedClass().equals(clazz)) {
                ExternalDataStoreObjectAdapter<A, S> adapter = (ExternalDataStoreObjectAdapter<A, S>) unknownAdapter;
                value = adapter.toAccessedObject((S) value);
                break;
            }
        }

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            return null;
        }
    }

    /**
     * Get data from the store, falling back to a default value.
     *
     * @param key          The key.
     * @param clazz        The class.
     * @param defaultValue The default value.
     * @param <T>          The type.
     * @return The value, or the default value if there is nothing stored under the key
     * or the stored value is not of the given type.
     */
    @NotNull
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz,
                            @NotNull final T defaultValue) {
        T value = get(key, clazz);
        return value == null ? defaultValue : value;
    }

    /**
     * Get data from the store, falling back to a supplied default value.
     * <p>
     * The supplier is always invoked, even when a stored value is present.
     *
     * @param key          The key.
     * @param clazz        The class.
     * @param defaultValue The supplier of the default value.
     * @param <T>          The type.
     * @return The value, or the supplied default if there is nothing stored under the key
     * or the stored value is not of the given type.
     */
    @NotNull
    public static <T> T get(@NotNull final String key,
                            @NotNull final Class<T> clazz,
                            @NotNull final Supplier<T> defaultValue) {
        return get(key, clazz, defaultValue.get());
    }

    /**
     * Register a new adapter to convert between accessed and stored objects.
     *
     * @param adapter The adapter.
     */
    public static void registerAdapter(@NotNull final ExternalDataStoreObjectAdapter<?, ?> adapter) {
        STORE_ADAPTERS.add(adapter);
    }

    /**
     * Utility class, cannot be instantiated.
     */
    private ExternalDataStore() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
