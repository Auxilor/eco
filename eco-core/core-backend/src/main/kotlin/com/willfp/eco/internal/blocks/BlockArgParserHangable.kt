package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Hangable

object BlockArgParserHangable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Hangable) return null
        var hanging: Boolean? = null

        for (arg in args) {
            if (arg.equals("hanging", true)) {
                hanging = true
            }
        }

        hanging ?: return null

        return BlockArgParseResult(
            {
                val hangable = it.blockData as? Hangable ?: return@BlockArgParseResult false

                hangable.isHanging == hanging
            },
            {
                val hangable = it.blockData as? Hangable ?: return@BlockArgParseResult

                hangable.isHanging = hanging
                it.blockData = hangable
            }
        )
    }
}