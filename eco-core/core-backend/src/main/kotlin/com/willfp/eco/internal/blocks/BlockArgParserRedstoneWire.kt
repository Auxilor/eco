package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.RedstoneWire

object BlockArgParserRedstoneWire : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val redstoneWire = blockData as? RedstoneWire ?: return null

        val faces = redstoneWire.allowedFaces.associateWith { RedstoneWire.Connection.NONE }.toMutableMap()

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("face", true)) {
                continue
            }
            if (argSplit.size < 3) {
                continue
            }

            val face = redstoneWire.allowedFaces.firstOrNull { it.name.equals(argSplit[1].uppercase(), true) } ?: continue
            faces[face] = runCatching { RedstoneWire.Connection.valueOf(argSplit[2].uppercase()) }.getOrNull() ?: continue
        }

        return BlockArgParseResult(
            {
                val redstoneWire = it.blockData as? RedstoneWire ?: return@BlockArgParseResult false

                redstoneWire.allowedFaces.all { face ->
                    faces[face] == redstoneWire.getFace(face)
                }
            },
            {
                val redstoneWire = it.blockData as? RedstoneWire ?: return@BlockArgParseResult

                faces.forEach { facing -> redstoneWire.setFace(facing.key, facing.value) }

                it.blockData = redstoneWire
            }
        )
    }
}