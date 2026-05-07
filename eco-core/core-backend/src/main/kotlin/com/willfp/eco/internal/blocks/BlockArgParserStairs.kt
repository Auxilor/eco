package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Stairs

object BlockArgParserStairs : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Stairs) return null
        var shape: Stairs.Shape? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("shape", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            shape = runCatching { Stairs.Shape.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        shape ?: return null

        return BlockArgParseResult(
            {
                val stairs = it.blockData as? Stairs ?: return@BlockArgParseResult false

                stairs.shape == shape
            },
            {
                val stairs = it.blockData as? Stairs ?: return@BlockArgParseResult

                stairs.shape = shape
                it.blockData = stairs
            }
        )
    }
}