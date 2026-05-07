package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.TNT

object BlockArgParserTNT : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is TNT) return null
        var unstable: Boolean? = null

        for (arg in args) {
            if (arg.equals("unstable", true)) {
                unstable = true
            }
        }

        unstable ?: return null

        return BlockArgParseResult(
            {
                val tnt = it.blockData as? TNT ?: return@BlockArgParseResult false

                tnt.isUnstable == unstable
            },
            {
                val tnt = it.blockData as? TNT ?: return@BlockArgParseResult

                tnt.isUnstable = unstable
                it.blockData = tnt
            }
        )
    }
}