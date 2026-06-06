package com.willfp.eco.core.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface EcoCache<K, V> {
    @Nullable V get(@NotNull K key);

    @NotNull V get(@NotNull K key, @NotNull Function<? super K, ? extends V> loader);

    void put(@NotNull K key, @NotNull V value);

    void invalidate(@NotNull K key);

    void invalidateAll();

    static <K, V> @NotNull EcoCacheBuilder<K, V> builder() {
        return new EcoCacheBuilder<>();
    }
}
