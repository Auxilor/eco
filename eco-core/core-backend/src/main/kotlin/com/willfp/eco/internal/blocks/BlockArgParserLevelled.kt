package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Levelled

object BlockArgParserLevelled : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var level: Int? = null

        val levelled = blockData as? Levelled ?: return null
        val maximumLevel = levelled.maximumLevel

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("level", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argLevel = argSplit[1].toIntOrNull() ?: continue
            if (argLevel in (maximumLevel + 1)..<0) {
                continue
            }
            level = argLevel
        }

        level ?: return null

        levelled.level = level

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