package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.HangingMoss

object BlockArgParserHangingMoss : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var tip: Boolean? = null

        val hangingMoss = blockData as? HangingMoss ?: return null

        for (arg in args) {
            if (arg.equals("tip", true)) {
                tip = true
            }
        }

        tip ?: return null

        hangingMoss.isTip = tip

        return BlockArgParseResult(
            {
                val hangingMoss = it.blockData as? HangingMoss ?: return@BlockArgParseResult false

                hangingMoss.isTip == tip
            },
            {
                val hangingMoss = it.blockData as? HangingMoss ?: return@BlockArgParseResult

                hangingMoss.isTip = tip
                it.blockData = hangingMoss
            }
        )
    }
}