@file:JvmName("ListUtilsExtensions")

package com.willfp.eco.util

/**
 * @see ListUtils.listToFrequencyMap
 */
fun <T> List<T>.toFrequencyMap(): Map<T, Int> =
    ListUtils.listToFrequencyMap(this)

/**
 * @see ListUtils.containsIgnoreCase
 */
fun Iterable<String>.containsIgnoreCase(element: String): Boolean =
    ListUtils.containsIgnoreCase(this, element)
