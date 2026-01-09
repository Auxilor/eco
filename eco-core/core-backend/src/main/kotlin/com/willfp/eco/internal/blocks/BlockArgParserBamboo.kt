package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Bamboo

object BlockArgParserBamboo : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var bambooLeaves: Bamboo.Leaves? = null

        val bamboo = blockData as? Bamboo ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("bamboo_leaves", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            bambooLeaves = runCatching { Bamboo.Leaves.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        bambooLeaves ?: return null

        bamboo.leaves = bambooLeaves

        return BlockArgParseResult(
            {
                val bamboo = it.blockData as? Bamboo ?: return@BlockArgParseResult false

                bamboo.leaves == bambooLeaves
            },
            {
                val bamboo = it.blockData as? Bamboo ?: return@BlockArgParseResult

                bamboo.leaves = bambooLeaves
                it.blockData = bamboo
            }
        )
    }
}