package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Tripwire

object BlockArgParserTripwire : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Tripwire) return null
        var disarmed: Boolean? = null

        for (arg in args) {
            if (arg.equals("disarmed", true)) {
                disarmed = true
            }
        }

        disarmed ?: return null

        return BlockArgParseResult(
            {
                val tripwire = it.blockData as? Tripwire ?: return@BlockArgParseResult false

                tripwire.isDisarmed == disarmed
            },
            {
                val tripwire = it.blockData as? Tripwire ?: return@BlockArgParseResult

                tripwire.isDisarmed = disarmed
                it.blockData = tripwire
            }
        )
    }
}