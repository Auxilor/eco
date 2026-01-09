package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.BubbleColumn

object BlockArgParserBubbleColumn : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var drag: Boolean? = null

        val bubbleColumn = blockData as? BubbleColumn ?: return null

        for (arg in args) {
            if (arg.equals("drag", true)) {
                drag = true
            }
        }

        drag ?: return null

        bubbleColumn.isDrag = drag

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