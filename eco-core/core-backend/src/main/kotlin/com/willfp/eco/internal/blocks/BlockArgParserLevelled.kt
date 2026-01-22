package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Levelled

object BlockArgParserLevelled : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val levelled = blockData as? Levelled ?: return null
        var level: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("level", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argLevel = argSplit[1].toIntOrNull() ?: continue
            if (argLevel in (levelled.maximumLevel + 1)..<0) {
                continue
            }
            level = argLevel
        }

        level ?: return null

        return BlockArgParseResult(
            {
                val levelled = it.blockData as? Levelled ?: return@BlockArgParseResult false

                levelled.level == level
            },
            {
                val levelled = it.blockData as? Levelled ?: return@BlockArgParseResult

                levelled.level = level
                it.blockData = levelled
            }
        )
    }
}