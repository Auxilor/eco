package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.Attachable
import org.bukkit.block.data.BlockData

object BlockArgParserAttachable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var attached: Boolean? = null

        val attachable = blockData as? Attachable ?: return null

        for (arg in args) {
            if (arg.equals("attached", true)) {
                attached = true
            }
        }

        attached ?: return null

        attachable.isAttached = attached

        return BlockArgParseResult(
            {
                val attachable = it.blockData as? Attachable ?: return@BlockArgParseResult false

                attachable.isAttached == attached
            },
            {
                val attachable = it.blockData as? Attachable ?: return@BlockArgParseResult

                attachable.isAttached = attached
                it.blockData = attachable
            }
        )
    }
}