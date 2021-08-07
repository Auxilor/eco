package com.willfp.eco.proxy.v1_17_R1

import org.bukkit.block.Block
import org.bukkit.entity.Player
import proxy.BlockBreakProxy

class BlockBreak : BlockBreakProxy {
    override fun breakBlock(
        player: Player,
        block: Block
    ) {
        player.breakBlock(block)
    }
}