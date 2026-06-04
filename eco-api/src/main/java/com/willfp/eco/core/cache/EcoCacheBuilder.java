package com.willfp.eco.core.cache;

import com.willfp.eco.internal.cache.CaffeineEcoCache;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

public final class EcoCacheBuilder<K, V> {
    private Duration expireAfterWrite = null;
    private Duration expireAfterAccess = null;
    private long maxSize = -1;

    public @NotNull EcoCacheBuilder<K, V> expireAfterWrite(@NotNull Duration duration) {
        this.expireAfterWrite = duration;
        return this;
    }

    public @NotNull EcoCacheBuilder<K, V> expireAfterAccess(@NotNull Duration duration) {
        this.expireAfterAccess = duration;
        return this;
    }

    public @NotNull EcoCacheBuilder<K, V> maxSize(long size) {
        this.maxSize = size;
        return this;
    }

    public @NotNull EcoCache<K, V> build() {
        return new CaffeineEcoCache<>(expireAfterWrite, expireAfterAccess, maxSize, null);
    }

    public @NotNull EcoCache<K, V> build(@NotNull Function<? super K, ? extends V> loader) {
        return new CaffeineEcoCache<>(expireAfterWrite, expireAfterAccess, maxSize, loader);
    }
}
