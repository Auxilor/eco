@file:JvmName("ArrowUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.entity.Arrow
import org.bukkit.inventory.ItemStack

/**
 * The bow that this arrow was shot from, read from the arrow's `shot-from` metadata.
 *
 * Null if the arrow has no such metadata (for example if it was not shot from a bow,
 * or if it was spawned by something that does not track the shooting item).
 *
 * @see ArrowUtils.getBow
 */
val Arrow.bow: ItemStack?
    get() = ArrowUtils.getBow(this)
