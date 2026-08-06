package com.willfp.eco.core.cache;

import com.willfp.eco.internal.cache.CaffeineEcoCache;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

/**
 * Builder for {@link EcoCache}.
 * <p>
 * Obtain an instance with {@link EcoCache#builder()}. All settings are optional; a cache built
 * without any settings is unbounded and never expires.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public final class EcoCacheBuilder<K, V> {
    /**
     * The duration after which entries expire once written, or null for no write expiry.
     */
    private Duration expireAfterWrite = null;

    /**
     * The duration after which entries expire once last accessed, or null for no access expiry.
     */
    private Duration expireAfterAccess = null;

    /**
     * The maximum number of entries, or a negative value for no size limit.
     */
    private long maxSize = -1;

    /**
     * Expire entries after the given duration has elapsed since they were last written.
     *
     * @param duration The duration.
     * @return This builder.
     */
    public @NotNull EcoCacheBuilder<K, V> expireAfterWrite(@NotNull Duration duration) {
        this.expireAfterWrite = duration;
        return this;
    }

    /**
     * Expire entries after the given duration has elapsed since they were last read or written.
     *
     * @param duration The duration.
     * @return This builder.
     */
    public @NotNull EcoCacheBuilder<K, V> expireAfterAccess(@NotNull Duration duration) {
        this.expireAfterAccess = duration;
        return this;
    }

    /**
     * Limit the cache to approximately the given number of entries, evicting entries once it is
     * exceeded.
     * <p>
     * A negative size means no limit is applied.
     *
     * @param size The maximum size.
     * @return This builder.
     */
    public @NotNull EcoCacheBuilder<K, V> maxSize(long size) {
        this.maxSize = size;
        return this;
    }

    /**
     * Build a cache with no loader.
     * <p>
     * {@link EcoCache#get(Object)} will return null for absent keys.
     *
     * @return The built cache.
     */
    public @NotNull EcoCache<K, V> build() {
        return new CaffeineEcoCache<>(expireAfterWrite, expireAfterAccess, maxSize, null);
    }

    /**
     * Build a cache backed by a loader.
     * <p>
     * {@link EcoCache#get(Object)} will compute and store a value with the loader for absent keys.
     *
     * @param loader The loader.
     * @return The built cache.
     */
    public @NotNull EcoCache<K, V> build(@NotNull Function<? super K, ? extends V> loader) {
        return new CaffeineEcoCache<>(expireAfterWrite, expireAfterAccess, maxSize, loader);
    }
}
