package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Dispenser

object BlockArgParserDispenser : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var triggered: Boolean? = null

        val dispenser = blockData as? Dispenser ?: return null

        for (arg in args) {
            if (arg.equals("triggered", true)) {
                triggered = true
            }
        }

        triggered ?: return null

        dispenser.isTriggered = triggered

        return BlockArgParseResult(
            {
                val dispenser = it.blockData as? Dispenser ?: return@BlockArgParseResult false

                dispenser.isTriggered == triggered
            },
            {
                val dispenser = it.blockData as? Dispenser ?: return@BlockArgParseResult

                dispenser.isTriggered = triggered
                it.blockData = dispenser
            }
        )
    }
}