package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Crafter

object BlockArgParserCrafter : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var orientation: Crafter.Orientation? = null
        var triggered: Boolean? = null
        var crafting: Boolean? = null

        val crafter = blockData as? Crafter ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("orientation", ignoreCase = true)) {
                if (argSplit.size < 2) {
                    continue
                }
                orientation =
                    runCatching { Crafter.Orientation.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
            } else if (argSplit[0].equals("triggered", ignoreCase = true)) {
                triggered = true
            } else if (argSplit[0].equals("crafting", ignoreCase = true)) {
                crafting = true
            }
        }

        if (orientation != null) {
            crafter.orientation = orientation
        }

        if (triggered != null) {
            crafter.isTriggered = triggered
        }

        if (crafting != null) {
            crafter.isCrafting = crafting
        }

        return BlockArgParseResult(
            {
                val crafter = it.blockData as? Crafter ?: return@BlockArgParseResult false

                (orientation == null || crafter.orientation == orientation) &&
                        (triggered == null || crafter.isTriggered == triggered) &&
                        (crafting == null || crafter.isCrafting == crafting)
            },
            {
                val crafter = it.blockData as? Crafter ?: return@BlockArgParseResult

                if (orientation != null) {
                    crafter.orientation = orientation
                }

                if (triggered != null) {
                    crafter.isTriggered = triggered
                }

                if (crafting != null) {
                    crafter.isCrafting = crafting
                }

                it.blockData = crafter
            }
        )
    }
}