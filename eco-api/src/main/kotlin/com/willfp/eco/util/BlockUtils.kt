@file:JvmName("BlockUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.block.Block

/**
 * If this block was placed by a player.
 *
 * Determined by looking up the block's location in its chunk's persistent data container,
 * so this only holds for blocks tracked by eco's player-placed block registry.
 *
 * @see BlockUtils.isPlayerPlaced
 */
val Block.isPlayerPlaced: Boolean
    get() = BlockUtils.isPlayerPlaced(this)
