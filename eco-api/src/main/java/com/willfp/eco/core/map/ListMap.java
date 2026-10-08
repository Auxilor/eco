package com.willfp.eco.core.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
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
     * Create a new list map.
     *
     * @param map  The backing map.
     * @param list The supplier of new, empty lists.
     */
    protected ListMap(@NotNull final Map<K, List<V>> map,
                      @NotNull final Supplier<List<V>> list) {
        super(map, list);
    }

    /**
     * Create a new thread-safe list map.
     * <p>
     * Backed by a {@link ConcurrentHashMap} of {@link CopyOnWriteArrayList}s, so it suits lists
     * that are iterated far more often than they are appended to. Null keys and values are
     * rejected.
     *
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The list map.
     */
    @NotNull
    public static <K, V> ListMap<K, V> concurrent() {
        return new ListMap<>(new ConcurrentHashMap<>(), CopyOnWriteArrayList::new);
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
