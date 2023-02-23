package com.willfp.eco.core.map;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps keys to lists of values.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class ListMap<K, V> extends DefaultMap<K, List<V>> {
    /**
     * Create a new list map.
     */
    public ListMap() {
        super(new ArrayList<>());
    }

    /**
     * Append a value to a key.
     *
     * @param key   The key.
     * @param value The value.
     */
    void append(@NotNull final K key,
                @NotNull final V value) {
        this.get(key).add(value);
    }
}

