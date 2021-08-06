package com.willfp.eco.proxy.v1_17_R1

import com.willfp.eco.proxy.BlockBreakProxy
import org.bukkit.block.Block
import org.bukkit.entity.Player

class BlockBreak : BlockBreakProxy {
    override fun breakBlock(
        player: Player,
        block: Block
    ) {
        player.breakBlock(block)
    }
}