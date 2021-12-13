package com.willfp.eco.internal.spigot.proxy

import org.bukkit.block.Block
import org.bukkit.entity.Player

interface BlockBreakProxy {
    fun breakBlock(
        player: Player,
        block: Block
    )
}