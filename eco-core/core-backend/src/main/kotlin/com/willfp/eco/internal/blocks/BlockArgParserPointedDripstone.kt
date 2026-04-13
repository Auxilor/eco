package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.PointedDripstone

object BlockArgParserPointedDripstone : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val pointedDripstone = blockData as? PointedDripstone ?: return null

        var thickness: PointedDripstone.Thickness? = null
        var verticalDirection: BlockFace? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("direction", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                verticalDirection = pointedDripstone.verticalDirections.firstOrNull {
                    it.name.equals(argSplit[1].uppercase(), true)
                } ?: continue
            } else if (argSplit[0].equals("thickness", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                thickness =
                    runCatching { PointedDripstone.Thickness.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
            }

        }

        if (verticalDirection == null && thickness == null) return null

        return BlockArgParseResult(
            {
                val pointedDripstone = it.blockData as? PointedDripstone ?: return@BlockArgParseResult false

                (verticalDirection == null || pointedDripstone.verticalDirection == verticalDirection) &&
                        (thickness == null || pointedDripstone.thickness == thickness)
            },
            {
                val pointedDripstone = it.blockData as? PointedDripstone ?: return@BlockArgParseResult

                if (verticalDirection != null) pointedDripstone.verticalDirection = verticalDirection
                if (thickness != null) pointedDripstone.thickness = thickness

                it.blockData = pointedDripstone
            }
        )
    }
}