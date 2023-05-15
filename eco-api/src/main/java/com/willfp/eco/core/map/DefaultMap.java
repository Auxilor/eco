package com.willfp.eco.core.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A map with a default value.
 *
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class DefaultMap<K, V> implements Map<K, V> {
    /**
     * The map.
     */
    private final Map<K, V> map;

    /**
     * The default value.
     */
    private final Supplier<V> defaultValue;

    /**
     * Create a new default map.
     *
     * @param defaultValue The default value.
     */
    public DefaultMap(@NotNull final V defaultValue) {
        this(() -> defaultValue);
    }

    /**
     * Create a new default map.
     *
     * @param defaultValue The default value.
     */
    public DefaultMap(@NotNull final Supplier<V> defaultValue) {
        this(new HashMap<>(), defaultValue);
    }

    /**
     * Create a new default map.
     *
     * @param map          The map.
     * @param defaultValue The default value.
     */
    public DefaultMap(@NotNull final Map<K, V> map,
                      @NotNull final V defaultValue) {
        this(map, () -> defaultValue);
    }

    /**
     * Create a new default map.
     *
     * @param map          The map.
     * @param defaultValue The default value.
     */
    public DefaultMap(@NotNull final Map<K, V> map,
                      @NotNull final Supplier<V> defaultValue) {
        this.map = map;
        this.defaultValue = defaultValue;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public V get(@Nullable final Object key) {
        if (key == null) {
            return defaultValue.get();
        }

        if (map.get(key) == null) {
            map.put((K) key, defaultValue.get());
        }

        return map.get(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable final Object value) {
        return map.containsValue(value);
    }

    @Override
    public V put(@NotNull final K key, @Nullable final V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(@NotNull final Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull final Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    /**
     * Create a new nested map.
     *
     * @param <K>  The key type.
     * @param <K1> The nested key type.
     * @param <V>  The value type.
     * @return The nested map.
     */
    @NotNull
    public static <K, K1, V> DefaultMap<K, Map<K1, V>> createNestedMap() {
        return new DefaultMap<>(HashMap::new);
    }

    /**
     * Create a new nested list map.
     *
     * @param <K>  The key type.
     * @param <K1> The nested key type.
     * @param <V>  The value type.
     * @return The nested list map.
     */
    @NotNull
    public static <K, K1, V> DefaultMap<K, ListMap<K1, V>> createNestedListMap() {
        return new DefaultMap<>(ListMap::new);
    }
}
