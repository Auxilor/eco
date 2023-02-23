package com.willfp.eco.core.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps keys to lists of values.
 *
 * @param <K>  The key type.
 * @param <K1> The key type of the map.
 * @param <V>  The value type of th emap.
 */
public class MappedMap<K, K1, V> extends DefaultMap<K, Map<K1, V>> {
    /**
     * Create a new list map.
     */
    public MappedMap() {
        super(new HashMap<>());
    }
}
