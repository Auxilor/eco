package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Wall

object BlockArgParserWall : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Wall) return null

        val faces = BlockFace.entries.associateWith { Wall.Height.NONE }.toMutableMap()
        var up: Boolean? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("face", true)) {
                if (argSplit.size < 3) {
                    continue
                }

                val face = runCatching { BlockFace.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
                faces[face] = runCatching { Wall.Height.valueOf(argSplit[2].uppercase()) }.getOrNull() ?: continue
            } else if (argSplit[0].equals("up", true)) {
                up = true
            }
        }

        return BlockArgParseResult(
            {
                val wall = it.blockData as? Wall ?: return@BlockArgParseResult false

                BlockFace.entries.all { face ->
                    faces[face] == wall.getHeight(face)
                } && (up == null || wall.isUp == up)
            },
            {
                val wall = it.blockData as? Wall ?: return@BlockArgParseResult

                faces.forEach { facing -> wall.setHeight(facing.key, facing.value) }
                if (up != null) wall.isUp = up

                it.blockData = wall
            }
        )
    }
}