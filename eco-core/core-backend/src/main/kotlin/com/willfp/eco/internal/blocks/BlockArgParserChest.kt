package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Chest

object BlockArgParserChest : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var chestType: Chest.Type? = null

        val chest = blockData as? Chest ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("chest_type", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            chestType = runCatching { Chest.Type.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        chestType ?: return null

        chest.type = chestType

        return BlockArgParseResult(
            {
                val chest = it.blockData as? Chest ?: return@BlockArgParseResult false

                chest.type == chestType
            },
            {
                val chest = it.blockData as? Chest ?: return@BlockArgParseResult

                chest.type = chestType
                it.blockData = chest
            }
        )
    }
}