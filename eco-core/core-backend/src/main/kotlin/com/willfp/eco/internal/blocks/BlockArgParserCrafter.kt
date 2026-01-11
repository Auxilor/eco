package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Crafter

object BlockArgParserCrafter : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Crafter) return null

        var orientation: Crafter.Orientation? = null
        var triggered: Boolean? = null
        var crafting: Boolean? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("orientation", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                orientation =
                    runCatching { Crafter.Orientation.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
            } else if (argSplit[0].equals("triggered", true)) {
                triggered = true
            } else if (argSplit[0].equals("crafting", true)) {
                crafting = true
            }
        }

        if (orientation == null && triggered == null && crafting == null) return null

        return BlockArgParseResult(
            {
                val crafter = it.blockData as? Crafter ?: return@BlockArgParseResult false

                (orientation == null || crafter.orientation == orientation) &&
                        (triggered == null || crafter.isTriggered == triggered) &&
                        (crafting == null || crafter.isCrafting == crafting)
            },
            {
                val crafter = it.blockData as? Crafter ?: return@BlockArgParseResult

                if (orientation != null) crafter.orientation = orientation
                if (triggered != null) crafter.isTriggered = triggered
                if (crafting != null) crafter.isCrafting = crafting

                it.blockData = crafter
            }
        )
    }
}