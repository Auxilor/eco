package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Bed

object BlockArgParserBed : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var bedPart: Bed.Part? = null

        val bed = blockData as? Bed ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("bed_part", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            bedPart = runCatching { Bed.Part.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        bedPart ?: return null

        bed.part = bedPart

        return BlockArgParseResult(
            {
                val bed = it.blockData as? Bed ?: return@BlockArgParseResult false

                bed.part == bedPart
            },
            {
                val bed = it.blockData as? Bed ?: return@BlockArgParseResult

                bed.part = bedPart
                it.blockData = bed
            }
        )
    }
}