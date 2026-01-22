package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.CommandBlock

object BlockArgParserCommandBlock : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is CommandBlock) return null
        var conditional: Boolean? = null

        for (arg in args) {
            if (arg.equals("conditional", true)) {
                conditional = true
            }
        }

        conditional ?: return null

        return BlockArgParseResult(
            {
                val commandBlock = it.blockData as? CommandBlock ?: return@BlockArgParseResult false

                commandBlock.isConditional == conditional
            },
            {
                val commandBlock = it.blockData as? CommandBlock ?: return@BlockArgParseResult

                commandBlock.isConditional = conditional
                it.blockData = commandBlock
            }
        )
    }
}