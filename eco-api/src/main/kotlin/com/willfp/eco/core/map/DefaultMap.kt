@file:JvmName("DefaultMapExtensions")

package com.willfp.eco.core.map

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier

/**
 * Required to avoid type ambiguity.
 *
 * @see ListMap
 */
@Suppress("RedundantOverride")
class MutableListMap<K : Any, V> : ListMap<K, V> {
    /**
     * Create a new list map, backed by a [HashMap] of [ArrayList]s.
     */
    constructor() : super()

    private constructor(
        map: MutableMap<K, List<V>>,
        list: Supplier<List<V>>
    ) : super(map, list)

    /**
     * Override with enforced MutableList type.
     */
    override fun get(key: K?): MutableList<V> =
        super.get(key)

    /**
     * Override with enforced MutableList type.
     */
    override fun getOrDefault(key: K, defaultValue: MutableList<V>): MutableList<V> {
        return super.getOrDefault(key, defaultValue)
    }

    companion object {
        /**
         * Create a new thread-safe list map, backed by a [ConcurrentHashMap] of
         * [CopyOnWriteArrayList]s. Null keys and values are rejected.
         *
         * @return The map.
         */
        @JvmStatic
        fun <K : Any, V> concurrent(): MutableListMap<K, V> =
            MutableListMap(ConcurrentHashMap(), Supplier { CopyOnWriteArrayList() })
    }
}

/**
 * Create a [DefaultMap] with a fixed default value.
 *
 * @param defaultValue The default value, shared by every missing key.
 * @return The map.
 * @see DefaultMap
 */
fun <K : Any, V : Any> defaultMap(defaultValue: V) =
    DefaultMap<K, V>(defaultValue)

/**
 * Create a [DefaultMap] with a default value produced by a function.
 *
 * The function is invoked once, eagerly, and the resulting value is then shared by every
 * missing key. It is not re-invoked per key.
 *
 * @param defaultValue The function producing the default value.
 * @return The map.
 * @see DefaultMap
 */
fun <K : Any, V : Any> defaultMap(defaultValue: () -> V) =
    DefaultMap<K, V>(defaultValue())

/**
 * Create a [MutableListMap], a [ListMap] that returns [MutableList] values.
 *
 * @return The map.
 * @see ListMap
 */
fun <K : Any, V : Any> listMap() =
    MutableListMap<K, V>()

/**
 * Create a [DefaultMap] of keys to maps, where missing keys default to a new, empty map.
 *
 * @return The map.
 * @see DefaultMap.createNestedMap
 */
fun <K : Any, K1 : Any, V> nestedMap() =
    DefaultMap.createNestedMap<K, K1, V>()

/**
 * Create a [DefaultMap] of keys to [MutableListMap]s, where missing keys default to a new,
 * empty [MutableListMap].
 *
 * @return The map.
 * @see DefaultMap.createNestedListMap
 */
fun <K : Any, K1 : Any, V> nestedListMap() =
    DefaultMap<K, MutableListMap<K1, V>> {
        MutableListMap()
    }

/**
 * Create a thread-safe [MutableListMap], backed by a [ConcurrentHashMap] of
 * [CopyOnWriteArrayList]s.
 *
 * @return The map.
 * @see ListMap.concurrent
 */
fun <K : Any, V : Any> concurrentListMap() =
    MutableListMap.concurrent<K, V>()

/**
 * Create a thread-safe [DefaultMap] of keys to maps, where missing keys default to a new, empty
 * [ConcurrentHashMap].
 *
 * @return The map.
 * @see DefaultMap.createConcurrentNestedMap
 */
fun <K : Any, K1 : Any, V> concurrentNestedMap() =
    DefaultMap.createConcurrentNestedMap<K, K1, V>()

/**
 * Create a thread-safe [DefaultMap] of keys to [MutableListMap]s, where missing keys default to a
 * new, empty thread-safe [MutableListMap].
 *
 * @return The map.
 * @see DefaultMap.createConcurrentNestedListMap
 */
fun <K : Any, K1 : Any, V> concurrentNestedListMap() =
    DefaultMap<K, MutableListMap<K1, V>>(ConcurrentHashMap()) {
        MutableListMap.concurrent()
    }
