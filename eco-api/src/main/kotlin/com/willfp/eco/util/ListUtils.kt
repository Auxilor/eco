@file:JvmName("ListUtilsExtensions")

package com.willfp.eco.util

/** @see ListUtils.listToFrequencyMap */
fun <T> List<T>.toFrequencyMap(): Map<T, Int> =
    ListUtils.listToFrequencyMap(this)

/** @see ListUtils.containsIgnoreCase */
fun Iterable<String>.containsIgnoreCase(element: String): Boolean =
    ListUtils.containsIgnoreCase(this, element)

/** @see ListUtils.create2DList */
fun <T> create2DList(rows: Int, columns: Int): MutableList<MutableList<T>> =
    ListUtils.create2DList(rows, columns)

/** @see ListUtils.toSingletonList */
fun <T> T?.toSingletonList(): List<T> =
    ListUtils.toSingletonList(this)
