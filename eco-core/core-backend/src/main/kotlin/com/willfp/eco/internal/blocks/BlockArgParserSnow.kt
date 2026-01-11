package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Snow

object BlockArgParserSnow : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val snow = blockData as? Snow ?: return null
        var layers: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("layers", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argLayers = argSplit[1].toIntOrNull() ?: continue
            if (argLayers in (snow.maximumLayers + 1)..<snow.minimumLayers) {
                continue
            }
            layers = argLayers
        }

        layers ?: return null

        return BlockArgParseResult(
            {
                val snow = it.blockData as? Snow ?: return@BlockArgParseResult false

                snow.layers == layers
            },
            {
                val snow = it.blockData as? Snow ?: return@BlockArgParseResult

                snow.layers = layers
                it.blockData = snow
            }
        )
    }
}