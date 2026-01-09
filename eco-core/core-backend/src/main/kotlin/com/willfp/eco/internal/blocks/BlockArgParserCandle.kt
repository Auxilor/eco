package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Candle

object BlockArgParserCandle : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var candles: Int? = null

        val candle = blockData as? Candle ?: return null
        val maximumCandles = candle.maximumCandles

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("candles", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argCandles = argSplit[1].toIntOrNull() ?: continue
            if (argCandles in (maximumCandles + 1)..<0) {
                continue
            }
            candles = argCandles
        }

        candles ?: return null

        candle.candles = candles

        return BlockArgParseResult(
            {
                val candle = it.blockData as? Candle ?: return@BlockArgParseResult false

                candle.candles == candles
            },
            {
                val candle = it.blockData as? Candle ?: return@BlockArgParseResult

                candle.candles = candles
                it.blockData = candle
            }
        )
    }
}