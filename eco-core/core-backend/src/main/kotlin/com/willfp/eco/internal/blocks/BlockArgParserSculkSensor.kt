package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.SculkSensor

object BlockArgParserSculkSensor : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is SculkSensor) return null
        var phase: SculkSensor.Phase? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("phase", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            phase = runCatching { SculkSensor.Phase.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        phase ?: return null

        return BlockArgParseResult(
            {
                val sculkSensor = it.blockData as? SculkSensor ?: return@BlockArgParseResult false

                sculkSensor.phase == phase
            },
            {
                val sculkSensor = it.blockData as? SculkSensor ?: return@BlockArgParseResult

                sculkSensor.phase = phase
                it.blockData = sculkSensor
            }
        )
    }
}