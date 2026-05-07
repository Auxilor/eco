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
 * @see DefaultMap
 */
fun <K : Any, V : Any> defaultMap(defaultValue: V) =
    DefaultMap<K, V>(defaultValue)

/**
 * @see DefaultMap
 */
fun <K : Any, V : Any> defaultMap(defaultValue: () -> V) =
    DefaultMap<K, V>(defaultValue())

/**
 * @see ListMap
 */
fun <K : Any, V : Any> listMap() =
    MutableListMap<K, V>()

/**
 * @see DefaultMap.createNestedMap
 */
fun <K : Any, K1 : Any, V> nestedMap() =
    DefaultMap.createNestedMap<K, K1, V>()

/**
 * @see DefaultMap.createNestedListMap
 */
fun <K : Any, K1 : Any, V> nestedListMap() =
    DefaultMap<K, MutableListMap<K1, V>> {
        MutableListMap()
    }
