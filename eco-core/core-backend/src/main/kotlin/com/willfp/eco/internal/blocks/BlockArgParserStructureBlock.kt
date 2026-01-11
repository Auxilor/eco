package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.StructureBlock

object BlockArgParserStructureBlock : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is StructureBlock) return null
        var mode: StructureBlock.Mode? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("mode", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            mode = runCatching { StructureBlock.Mode.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        mode ?: return null

        return BlockArgParseResult(
            {
                val structureBlock = it.blockData as? StructureBlock ?: return@BlockArgParseResult false

                structureBlock.mode == mode
            },
            {
                val structureBlock = it.blockData as? StructureBlock ?: return@BlockArgParseResult

                structureBlock.mode = mode
                it.blockData = structureBlock
            }
        )
    }
}