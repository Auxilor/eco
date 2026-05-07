package com.willfp.eco.core.map;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

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
        super(ArrayList::new);
    }

    /**
     * Append a value to a key.
     *
     * @param key   The key.
     * @param value The value.
     */
    public void append(@NotNull final K key,
                       @NotNull final V value) {
        this.get(key).add(value);
    }
}
