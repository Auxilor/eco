package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.MultipleFacing

object BlockArgParserMultipleFacing : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val multipleFacing = blockData as? MultipleFacing ?: return null

        val faces = multipleFacing.allowedFaces.associateWith { false }.toMutableMap()

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("face", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }

            val face = multipleFacing.allowedFaces.firstOrNull { it.name.equals(argSplit[1].uppercase(), true) } ?: continue
            faces[face] = true
        }

        return BlockArgParseResult(
            {
                val multipleFacing = it.blockData as? MultipleFacing ?: return@BlockArgParseResult false

                multipleFacing.allowedFaces.all { face ->
                    faces[face] == multipleFacing.hasFace(face)
                }
            },
            {
                val multipleFacing = it.blockData as? MultipleFacing ?: return@BlockArgParseResult

                faces.forEach { facing -> multipleFacing.setFace(facing.key, facing.value) }

                it.blockData = multipleFacing
            }
        )
    }
}