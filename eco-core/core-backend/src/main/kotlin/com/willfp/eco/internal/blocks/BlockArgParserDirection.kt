package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional

object BlockArgParserDirection : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var direction: BlockFace? = null

        val directional = blockData as? Directional ?: return null
        val directions = directional.faces

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("direction", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argDirection = BlockFace.valueOf(argSplit[1].uppercase())
            if (!directions.contains(argDirection)) {
                continue
            }
            direction = argDirection
        }

        direction ?: return null

        directional.facing = direction

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