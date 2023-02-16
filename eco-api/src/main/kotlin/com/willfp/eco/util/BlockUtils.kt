@file:JvmName("BlockUtilsExtensions")

package com.willfp.eco.util

import org.bukkit.block.Block
import org.bukkit.OfflinePlayer;

/** @see ArrowUtils.getBow */
val Block.isPlayerPlaced: Boolean
    get() = BlockUtils.isPlayerPlaced(this)

/** @see BlockUtils.IsPlacedBy */
fun Block.isPlacedBy(player: OfflinePlayer) =
    BlockUtils.isPlacedBy(this,player)
