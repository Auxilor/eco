package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Waterlogged

object BlockArgParserWaterlogged : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Waterlogged) return null
        var waterlogged: Boolean? = null

        for (arg in args) {
            if (arg.equals("waterlogged", true)) {
                waterlogged = true
            }
        }

        waterlogged ?: return null

        return BlockArgParseResult(
            {
                val waterloggedBlock = it.blockData as? Waterlogged ?: return@BlockArgParseResult false

                waterloggedBlock.isWaterlogged == waterlogged
            },
            {
                val waterloggedBlock = it.blockData as? Waterlogged ?: return@BlockArgParseResult

                waterloggedBlock.isWaterlogged = waterlogged
                it.blockData = waterloggedBlock
            }
        )
    }
}