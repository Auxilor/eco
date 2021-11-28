package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.BlockBreakProxy
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