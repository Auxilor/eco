package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Scaffolding

object BlockArgParserScaffolding : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val scaffolding = blockData as? Scaffolding ?: return null
        var distance: Int? = null
        var bottom: Boolean? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("distance", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                val argDistance = argSplit[1].toIntOrNull() ?: continue
                if (argDistance in (scaffolding.maximumDistance + 1)..<0) {
                    continue
                }
                distance = argDistance
            } else if (argSplit[0].equals("bottom", true)) {
                bottom = true
            }
        }

        if (distance == null && bottom == null) return null

        return BlockArgParseResult(
            {
                val scaffolding = it.blockData as? Scaffolding ?: return@BlockArgParseResult false

                (distance == null || scaffolding.distance == distance) && (bottom == null || scaffolding.isBottom == bottom)
            },
            {
                val scaffolding = it.blockData as? Scaffolding ?: return@BlockArgParseResult

                if (distance != null) scaffolding.distance = distance
                if (bottom != null) scaffolding.isBottom = bottom

                it.blockData = scaffolding
            }
        )
    }
}