package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.BlockData

object BlockArgParserBisected : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Bisected) return null
        var bisectedHalf: Bisected.Half? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("bisected", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            bisectedHalf = runCatching { Bisected.Half.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        bisectedHalf ?: return null

        return BlockArgParseResult(
            {
                val bisected = it.blockData as? Bisected ?: return@BlockArgParseResult false

                bisected.half == bisectedHalf
            },
            {
                val bisected = it.blockData as? Bisected ?: return@BlockArgParseResult

                bisected.half = bisectedHalf
                it.blockData = bisected
            }
        )
    }
}