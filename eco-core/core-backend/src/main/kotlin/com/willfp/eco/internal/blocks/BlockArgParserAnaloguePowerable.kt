package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.AnaloguePowerable
import org.bukkit.block.data.BlockData

object BlockArgParserAnaloguePowerable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var power: Int? = null

        val analoguePowerable = blockData as? AnaloguePowerable ?: return null
        val maximumPower = analoguePowerable.maximumPower

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("power", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argPower = argSplit[1].toIntOrNull() ?: continue
            if (argPower in (maximumPower + 1)..<0) {
                continue
            }
            power = argPower
        }

        power ?: return null

        analoguePowerable.power = power

        return BlockArgParseResult(
            {
                val analoguePowerable = it.blockData as? AnaloguePowerable ?: return@BlockArgParseResult false

                analoguePowerable.power == power
            },
            {
                val analoguePowerable = it.blockData as? AnaloguePowerable ?: return@BlockArgParseResult

                analoguePowerable.power = power
                it.blockData = analoguePowerable
            }
        )
    }
}