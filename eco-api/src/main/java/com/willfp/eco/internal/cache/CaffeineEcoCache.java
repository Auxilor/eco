package com.willfp.eco.internal.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.willfp.eco.core.cache.EcoCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Function;

public class CaffeineEcoCache<K, V> implements EcoCache<K, V> {
    private final Cache<K, V> cache;
    @Nullable
    private final LoadingCache<K, V> loadingCache;


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
