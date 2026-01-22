package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional

object BlockArgParserDirectional : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val directional = blockData as? Directional ?: return null
        var direction: BlockFace? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("direction", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            direction = directional.faces.firstOrNull { it.name.equals(argSplit[1].uppercase(), true) } ?: continue
        }

        direction ?: return null

        return BlockArgParseResult(
            {
                val directional = it.blockData as? Directional ?: return@BlockArgParseResult false

                directional.facing == direction
            },
            {
                val directional = it.blockData as? Directional ?: return@BlockArgParseResult

                directional.facing = direction
                it.blockData = directional
            }
        )
    }
}