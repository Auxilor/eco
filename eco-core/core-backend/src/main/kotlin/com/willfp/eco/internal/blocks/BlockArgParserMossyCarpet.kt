package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.MossyCarpet

object BlockArgParserMossyCarpet : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val mossyCarpetHeight = BlockFace.entries.associateWith { MossyCarpet.Height.NONE }.toMutableMap()
        var mossyCarpetBottom: Boolean? = null

        val mossyCarpet = blockData as? MossyCarpet ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("height", ignoreCase = true)) {
                if (argSplit.size < 3) {
                    continue
                }
                val face = runCatching { BlockFace.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
                val height = runCatching { MossyCarpet.Height.valueOf(argSplit[2].uppercase()) }.getOrNull() ?: continue
                mossyCarpetHeight += face to height
            } else if (argSplit[0].equals("bottom", ignoreCase = true)) {
                mossyCarpetBottom = true
            }
        }

        mossyCarpetHeight.forEach { mossyCarpet.setHeight(it.key, it.value) }

        if (mossyCarpetBottom != null) {
            mossyCarpet.isBottom = mossyCarpetBottom
        }

        return BlockArgParseResult(
            {
                val mossyCarpet = it.blockData as? MossyCarpet ?: return@BlockArgParseResult false

                BlockFace.entries.all { face ->
                    mossyCarpetHeight[face] != mossyCarpet.getHeight(face)
                } && (mossyCarpetBottom == null || mossyCarpet.isBottom == mossyCarpetBottom)
            },
            {
                val mossyCarpet = it.blockData as? MossyCarpet ?: return@BlockArgParseResult

                mossyCarpetHeight.forEach { carpet -> mossyCarpet.setHeight(carpet.key, carpet.value) }

                if (mossyCarpetBottom != null) {
                    mossyCarpet.isBottom = mossyCarpetBottom
                }

                it.blockData = mossyCarpet
            }
        )
    }
}