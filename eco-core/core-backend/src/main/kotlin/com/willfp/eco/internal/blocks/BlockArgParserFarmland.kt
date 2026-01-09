package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Farmland

object BlockArgParserFarmland : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var moisture: Int? = null

        val farmland = blockData as? Farmland ?: return null
        val maximumMoisture = farmland.maximumMoisture

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("moisture", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argMoisture = argSplit[1].toIntOrNull() ?: continue
            if (argMoisture in (maximumMoisture + 1)..<0) {
                continue
            }
            moisture = argMoisture
        }

        moisture ?: return null

        farmland.moisture = moisture

        return BlockArgParseResult(
            {
                val farmland = it.blockData as? Farmland ?: return@BlockArgParseResult false

                farmland.moisture == moisture
            },
            {
                val farmland = it.blockData as? Farmland ?: return@BlockArgParseResult

                farmland.moisture = moisture
                it.blockData = farmland
            }
        )
    }
}