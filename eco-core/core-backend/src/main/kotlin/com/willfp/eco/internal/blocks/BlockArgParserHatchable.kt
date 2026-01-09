package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Hatchable

object BlockArgParserHatchable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var hatching: Int? = null

        val hatchable = blockData as? Hatchable ?: return null
        val maximumHatches = hatchable.maximumHatch

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("hatching", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argHatching = argSplit[1].toIntOrNull() ?: continue
            if (argHatching in (maximumHatches + 1)..<0) {
                continue
            }
            hatching = argHatching
        }

        hatching ?: return null

        hatchable.hatch = hatching

        return BlockArgParseResult(
            {
                val hatchable = it.blockData as? Hatchable ?: return@BlockArgParseResult false

                hatchable.hatch == hatching
            },
            {
                val hatchable = it.blockData as? Hatchable ?: return@BlockArgParseResult

                hatchable.hatch = hatching
                it.blockData = hatchable
            }
        )
    }
}