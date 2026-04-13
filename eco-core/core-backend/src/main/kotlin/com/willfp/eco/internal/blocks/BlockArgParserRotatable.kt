package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Rotatable

object BlockArgParserRotatable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Rotatable) return null
        var rotation: BlockFace? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("rotation", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            rotation = runCatching { BlockFace.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        rotation ?: return null

        return BlockArgParseResult(
            {
                val rotatable = it.blockData as? Rotatable ?: return@BlockArgParseResult false

                rotatable.rotation == rotation
            },
            {
                val rotatable = it.blockData as? Rotatable ?: return@BlockArgParseResult

                rotatable.rotation = rotation
                it.blockData = rotatable
            }
        )
    }
}