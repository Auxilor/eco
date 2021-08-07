package com.willfp.eco.proxy

import com.willfp.eco.core.proxy.AbstractProxy
import org.bukkit.block.Block
import org.bukkit.entity.Player

interface BlockBreakProxy : AbstractProxy {
    fun breakBlock(
        player: Player,
        block: Block
    )
}