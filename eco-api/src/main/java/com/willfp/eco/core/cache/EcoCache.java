package com.willfp.eco.core.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A cache with optional expiry and size-based eviction.
 * <p>
 * Create instances with {@link #builder()}.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public interface EcoCache<K, V> {
    /**
     * Get the value for a key.
     * <p>
     * If the cache was built with a loader (see {@link EcoCacheBuilder#build(Function)}), the loader
     * will be used to compute and store a value for keys that are absent. Otherwise, no value is
     * computed and null is returned for absent keys.
     *
     * @param key The key.
     * @return The value, or null if absent and the cache has no loader.
     */
    @Nullable V get(@NotNull K key);

    /**
     * Get the value for a key, computing it with the given loader if absent.
     * <p>
     * The loaded value is stored in the cache. This does not use the loader the cache was built with,
     * if any.
     *
     * @param key    The key.
     * @param loader The loader to compute the value with if the key is absent.
     * @return The cached or newly loaded value.
     */
    @NotNull V get(@NotNull K key, @NotNull Function<? super K, ? extends V> loader);

    /**
     * Store a value for a key, replacing any existing value.
     *
     * @param key   The key.
     * @param value The value.
     */
    void put(@NotNull K key, @NotNull V value);

    /**
     * Invalidate the entry for a key, if present.
     *
     * @param key The key.
     */
    void invalidate(@NotNull K key);

    /**
     * Invalidate all entries in the cache.
     */
    void invalidateAll();

    /**
     * Create a new cache builder.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The builder.
     */
    static <K, V> @NotNull EcoCacheBuilder<K, V> builder() {
        return new EcoCacheBuilder<>();
    }
}
