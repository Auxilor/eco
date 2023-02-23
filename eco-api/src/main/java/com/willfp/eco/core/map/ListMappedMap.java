package com.willfp.eco.core.map;

/**
 * Maps keys to lists of values.
 *
 * @param <K>  The key type.
 * @param <K1> The key type of the map.
 * @param <V>  The value type of the list in the map.
 */
public class ListMappedMap<K, K1, V> extends DefaultMap<K, ListMap<K1, V>> {
    /**
     * Create a new list map.
     */
    public ListMappedMap() {
        super(new ListMap<>());
    }
}
