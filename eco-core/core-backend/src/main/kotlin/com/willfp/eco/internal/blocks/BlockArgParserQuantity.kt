package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Hatchable
import org.bukkit.block.data.type.Candle
import org.bukkit.block.data.type.SeaPickle
import org.bukkit.block.data.type.TurtleEgg

object BlockArgParserQuantity : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val maximumQuantity = when (val blockData = blockData) {
            is TurtleEgg -> blockData.maximumEggs
            is Candle -> blockData.maximumCandles
            is SeaPickle -> blockData.maximumPickles
            is Hatchable -> blockData.maximumHatch
            else -> return null
        }
        val minimumQuantity = when (val blockData = blockData) {
            is TurtleEgg -> blockData.minimumEggs
            else -> 0
        }

        var quantity: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("quantity", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argQuantity = argSplit[1].toIntOrNull() ?: continue
            if (argQuantity in (maximumQuantity + 1)..<minimumQuantity) {
                continue
            }
            quantity = argQuantity
        }

        quantity ?: return null

        return BlockArgParseResult(
            {
                when (val data = it.blockData) {
                    is TurtleEgg -> data.eggs == quantity
                    is Candle -> data.candles == quantity
                    is SeaPickle -> data.pickles == quantity
                    is Hatchable -> data.hatch == quantity
                    else -> false
                }
            },
            {
                val data = it.blockData

                when (data) {
                    is Candle -> data.candles = quantity
                    is SeaPickle -> data.pickles = quantity
                    is TurtleEgg -> data.eggs = quantity
                    is Hatchable -> data.hatch = quantity
                    else -> return@BlockArgParseResult
                }

                it.blockData = data
            }
        )
    }
}