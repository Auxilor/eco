package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Cake

object BlockArgParserCake : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var bites: Int? = null

        val cake = blockData as? Cake ?: return null
        val maximumBites = cake.maximumBites

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("bites", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argBites = argSplit[1].toIntOrNull() ?: continue
            if (argBites in (maximumBites + 1)..<0) {
                continue
            }
            bites = argBites
        }

        bites ?: return null

        cake.bites = bites

        return BlockArgParseResult(
            {
                val cake = it.blockData as? Cake ?: return@BlockArgParseResult false

                cake.bites == bites
            },
            {
                val cake = it.blockData as? Cake ?: return@BlockArgParseResult

                cake.bites = bites
                it.blockData = cake
            }
        )
    }
}