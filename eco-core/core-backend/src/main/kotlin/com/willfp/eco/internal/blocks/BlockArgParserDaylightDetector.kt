package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.DaylightDetector

object BlockArgParserDaylightDetector : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is DaylightDetector) return null
        var inverted: Boolean? = null

        for (arg in args) {
            if (arg.equals("inverted", true)) {
                inverted = true
            }
        }

        inverted ?: return null

        return BlockArgParseResult(
            {
                val daylightDetector = it.blockData as? DaylightDetector ?: return@BlockArgParseResult false

                daylightDetector.isInverted == inverted
            },
            {
                val daylightDetector = it.blockData as? DaylightDetector ?: return@BlockArgParseResult

                daylightDetector.isInverted = inverted
                it.blockData = daylightDetector
            }
        )
    }
}