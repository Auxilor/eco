package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.BubbleColumn

object BlockArgParserBubbleColumn : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is BubbleColumn) return null
        var drag: Boolean? = null

        for (arg in args) {
            if (arg.equals("drag", true)) {
                drag = true
            }
        }

        drag ?: return null

        return BlockArgParseResult(
            {
                val bubbleColumn = it.blockData as? BubbleColumn ?: return@BlockArgParseResult false

                bubbleColumn.isDrag == drag
            },
            {
                val bubbleColumn = it.blockData as? BubbleColumn ?: return@BlockArgParseResult

                bubbleColumn.isDrag = drag
                it.blockData = bubbleColumn
            }
        )
    }
}