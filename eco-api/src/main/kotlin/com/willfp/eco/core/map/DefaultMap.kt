@file:JvmName("DefaultMapExtensions")

package com.willfp.eco.core.map

/**
 * @see DefaultMap
 */
fun <K : Any, V : Any> defaultMap(defaultValue: V) =
    DefaultMap<K, V>(defaultValue)

/**
 * @see ListMap
 */
fun <K : Any, V : Any> listMap() =
    ListMap<K, V>()

/**
 * @see DefaultMap.createNestedMap
 */
fun <K : Any, K1 : Any, V : Any> nestedMap() =
    DefaultMap.createNestedMap<K, K1, V>()

/**
 * @see DefaultMap.createNestedListMap
 */
fun <K : Any, K1 : Any, V : Any> nestedListMap() =
    DefaultMap.createNestedListMap<K, K1, V>()
