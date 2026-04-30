package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Rail

object BlockArgParserRail : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val rail = blockData as? Rail ?: return null
        var shape: Rail.Shape? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("shape", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            shape = rail.shapes.firstOrNull { it.name.equals(argSplit[1].uppercase(), true) } ?: continue
        }

        shape ?: return null

        return BlockArgParseResult(
            {
                val rail = it.blockData as? Rail ?: return@BlockArgParseResult false

                rail.shape == shape
            },
            {
                val rail = it.blockData as? Rail ?: return@BlockArgParseResult

                rail.shape = shape
                it.blockData = rail
            }
        )
    }
}