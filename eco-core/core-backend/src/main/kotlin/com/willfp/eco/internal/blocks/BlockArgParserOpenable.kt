package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Openable

object BlockArgParserOpenable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Openable) return null
        var open: Boolean? = null

        for (arg in args) {
            if (arg.equals("open", true)) {
                open = true
            }
        }

        open ?: return null

        return BlockArgParseResult(
            {
                val openable = it.blockData as? Openable ?: return@BlockArgParseResult false

                openable.isOpen == open
            },
            {
                val openable = it.blockData as? Openable ?: return@BlockArgParseResult

                openable.isOpen = open
                it.blockData = openable
            }
        )
    }
}