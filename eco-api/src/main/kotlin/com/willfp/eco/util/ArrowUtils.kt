@file:JvmName("ArrowUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.entity.Arrow
import org.bukkit.inventory.ItemStack

/** @see ArrowUtils.getBow */
val Arrow.bow: ItemStack?
    get() = ArrowUtils.getBow(this)
