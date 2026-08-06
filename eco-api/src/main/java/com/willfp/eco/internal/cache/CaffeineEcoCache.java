package com.willfp.eco.internal.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.cache.EcoCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Function;

/**
 * Implementation of {@link EcoCache} backed by Caffeine.
 * <p>
 * This is internal; create caches with {@link EcoCache#builder()} rather than constructing this
 * directly.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class CaffeineEcoCache<K, V> implements EcoCache<K, V> {
    /**
     * The backing Caffeine cache.
     */
    private final Cache<K, V> cache;

    /**
     * The backing cache as a {@link LoadingCache}, or null if the cache was built without a loader.
     */
    @Nullable
    private final LoadingCache<K, V> loadingCache;


    /**
     * Create a new Caffeine-backed cache.
     *
     * @param expireAfterWrite  The duration after which entries expire once written, or null for
     *                          no write expiry.
     * @param expireAfterAccess The duration after which entries expire once last accessed, or null
     *                          for no access expiry.
     * @param maxSize           The approximate maximum number of entries, or a negative value for
     *                          no size limit.
     * @param loader            The loader used to compute values for absent keys, or null to leave
     *                          absent keys unloaded.
     */
    public CaffeineEcoCache(
        @Nullable Duration expireAfterWrite,
        @Nullable Duration expireAfterAccess,
        long maxSize,
        @Nullable Function<? super K, ? extends V> loader
    ) {
        var builder = Caffeine.newBuilder();
        if (expireAfterWrite != null) {
            builder.expireAfterWrite(expireAfterWrite);
        }
        if (expireAfterAccess != null) {
            builder.expireAfterAccess(expireAfterAccess);
        }
        if (maxSize >= 0) {
            builder.maximumSize(maxSize);
        }
        if (loader != null) {
            LoadingCache<K, V> lc = builder.build(loader::apply);
            this.loadingCache = lc;
            this.cache = lc;
        } else {
            this.loadingCache = null;
            this.cache = builder.build();
        }
    }

    @Override
    public @Nullable V get(@NotNull K key) {
        if (loadingCache != null) {
            return loadingCache.get(key);
        }
        return cache.getIfPresent(key);
    }

    @Override
    public @NotNull V get(@NotNull K key, @NotNull Function<? super K, ? extends V> loader) {
        return cache.get(key, loader);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
    }

    @Override
    public void invalidate(@NotNull K key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
