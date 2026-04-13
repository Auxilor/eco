package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Snowable

object BlockArgParserSnowy : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Snowable) return null
        var snowy: Boolean? = null

        for (arg in args) {
            if (arg.equals("snowy", true)) {
                snowy = true
            }
        }

        snowy ?: return null

        return BlockArgParseResult(
            {
                val snowable = it.blockData as? Snowable ?: return@BlockArgParseResult false

                snowable.isSnowy == snowy
            },
            {
                val snowable = it.blockData as? Snowable ?: return@BlockArgParseResult

                snowable.isSnowy = snowy
                it.blockData = snowable
            }
        )
    }
}