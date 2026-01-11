package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Lightable

object BlockArgParserLightable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Lightable) return null
        var lit: Boolean? = null

        for (arg in args) {
            if (arg.equals("lit", true)) {
                lit = true
            }
        }

        lit ?: return null

        return BlockArgParseResult(
            {
                val lightable = it.blockData as? Lightable ?: return@BlockArgParseResult false

                lightable.isLit == lit
            },
            {
                val lightable = it.blockData as? Lightable ?: return@BlockArgParseResult

                lightable.isLit = lit
                it.blockData = lightable
            }
        )
    }
}