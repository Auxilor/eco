@file:JvmName("ListUtilsExtensions")

package com.willfp.eco.util

/**
 * Convert this list, which may contain duplicates, into a map of each distinct element to
 * the number of times it occurs in the list.
 *
 * @return The frequency map.
 * @see ListUtils.listToFrequencyMap
 */
fun <T> List<T>.toFrequencyMap(): Map<T, Int> =
    ListUtils.listToFrequencyMap(this)

/**
 * Get if this iterable contains an element, ignoring case.
 *
 * @param element The element to look for.
 * @return If an element equal to it, ignoring case, is contained.
 * @see ListUtils.containsIgnoreCase
 */
fun Iterable<String>.containsIgnoreCase(element: String): Boolean =
    ListUtils.containsIgnoreCase(this, element)

/**
 * Create a mutable 2D list of a fixed size, where every cell is initialised to null.
 *
 * The outer list holds `rows` inner lists, each holding `columns` elements. As the cells
 * start out null, `T` should be a nullable type.
 *
 * @param rows    The number of rows (the size of the outer list).
 * @param columns The number of columns (the size of each inner list).
 * @return The created list, filled with nulls.
 * @see ListUtils.create2DList
 */
fun <T> create2DList(rows: Int, columns: Int): MutableList<MutableList<T>> =
    ListUtils.create2DList(rows, columns)

/**
 * Convert this nullable object into a list containing it.
 *
 * @return An immutable singleton list containing the receiver, or an immutable empty list if it is null.
 * @see ListUtils.toSingletonList
 */
fun <T> T?.toSingletonList(): List<T> =
    ListUtils.toSingletonList(this)
