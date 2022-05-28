@file:JvmName("BlockUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.block.Block

/** @see ArrowUtils.getBow */
val Block.isPlayerPlaced: Boolean
    get() = BlockUtils.isPlayerPlaced(this)
