@file:JvmName("DefaultMapExtensions")

package com.willfp.eco.core.map

/**
 * Required to avoid type ambiguity.
 *
 * @see ListMap
 */
@Suppress("RedundantOverride")
class MutableListMap<K : Any, V> : ListMap<K, V>() {
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
