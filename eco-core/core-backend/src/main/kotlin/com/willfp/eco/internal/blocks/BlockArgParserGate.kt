package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Gate

object BlockArgParserGate : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var inWall: Boolean? = null

        val gate = blockData as? Gate ?: return null

        for (arg in args) {
            if (arg.equals("in_wall", true)) {
                inWall = true
            }
        }

        inWall ?: return null

        gate.isInWall = inWall

        return BlockArgParseResult(
            {
                val gate = it.blockData as? Gate ?: return@BlockArgParseResult false

                gate.isInWall == inWall
            },
            {
                val gate = it.blockData as? Gate ?: return@BlockArgParseResult

                gate.isInWall = inWall
                it.blockData = gate
            }
        )
    }
}