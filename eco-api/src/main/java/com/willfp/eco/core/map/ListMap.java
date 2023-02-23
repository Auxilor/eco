package com.willfp.eco.core.map;

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
}
