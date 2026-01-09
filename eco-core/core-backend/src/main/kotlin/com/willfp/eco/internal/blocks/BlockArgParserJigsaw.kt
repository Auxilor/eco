package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Jigsaw

object BlockArgParserJigsaw : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var orientation: Jigsaw.Orientation? = null

        val jigsaw = blockData as? Jigsaw ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("orientation", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            orientation = runCatching { Jigsaw.Orientation.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        orientation ?: return null

        jigsaw.orientation = orientation

        return BlockArgParseResult(
            {
                val jigsaw = it.blockData as? Jigsaw ?: return@BlockArgParseResult false

                jigsaw.orientation == orientation
            },
            {
                val jigsaw = it.blockData as? Jigsaw ?: return@BlockArgParseResult

                jigsaw.orientation = orientation
                it.blockData = jigsaw
            }
        )
    }
}