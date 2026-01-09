package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.DaylightDetector

object BlockArgParserDaylightDetector : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var inveted: Boolean? = null

        val daylightDetector = blockData as? DaylightDetector ?: return null

        for (arg in args) {
            if (arg.equals("inverted", true)) {
                inveted = true
            }
        }

        inveted ?: return null

        daylightDetector.isInverted = inveted

        return BlockArgParseResult(
            {
                val daylightDetector = it.blockData as? DaylightDetector ?: return@BlockArgParseResult false

                daylightDetector.isInverted == inveted
            },
            {
                val daylightDetector = it.blockData as? DaylightDetector ?: return@BlockArgParseResult

                daylightDetector.isInverted = inveted
                it.blockData = daylightDetector
            }
        )
    }
}