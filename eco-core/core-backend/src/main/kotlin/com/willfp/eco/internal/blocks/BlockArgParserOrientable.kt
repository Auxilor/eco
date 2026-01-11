package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.Axis
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Orientable

object BlockArgParserOrientable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val orientable = blockData as? Orientable ?: return null
        var axis: Axis? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("axis", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            axis = orientable.axes.firstOrNull { it.name.equals(argSplit[1].uppercase(), true) } ?: continue
        }

        axis ?: return null

        return BlockArgParseResult(
            {
                val orientable = it.blockData as? Orientable ?: return@BlockArgParseResult false

                orientable.axis == axis
            },
            {
                val orientable = it.blockData as? Orientable ?: return@BlockArgParseResult

                orientable.axis = axis
                it.blockData = orientable
            }
        )
    }
}