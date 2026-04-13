package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Comparator

object BlockArgParserComparator : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Comparator) return null
        var comparatorMode: Comparator.Mode? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("mode", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            comparatorMode = runCatching { Comparator.Mode.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        comparatorMode ?: return null

        return BlockArgParseResult(
            {
                val comparator = it.blockData as? Comparator ?: return@BlockArgParseResult false

                comparator.mode == comparatorMode
            },
            {
                val comparator = it.blockData as? Comparator ?: return@BlockArgParseResult

                comparator.mode = comparatorMode
                it.blockData = comparator
            }
        )
    }
}