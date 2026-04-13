package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.CaveVinesPlant

object BlockArgParserCaveVinesPlant : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is CaveVinesPlant) return null
        var berries: Boolean? = null

        for (arg in args) {
            if (arg.equals("berries", true)) {
                berries = true
            }
        }

        berries ?: return null

        return BlockArgParseResult(
            {
                val caveVinesPlant = it.blockData as? CaveVinesPlant ?: return@BlockArgParseResult false

                caveVinesPlant.isBerries == berries
            },
            {
                val caveVinesPlant = it.blockData as? CaveVinesPlant ?: return@BlockArgParseResult

                caveVinesPlant.isBerries = berries
                it.blockData = caveVinesPlant
            }
        )
    }
}