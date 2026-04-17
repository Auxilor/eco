package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.SculkShrieker

object BlockArgParserSculkShrieker : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is SculkShrieker) return null
        var canSummon: Boolean? = null
        var shrieking: Boolean? = null

        for (arg in args) {
            if (arg.equals("can_summon", true)) {
                canSummon = true
            } else if (arg.equals("shrieking", true)) {
                shrieking = true
            }
        }

        if (canSummon == null && shrieking == null) return null

        return BlockArgParseResult(
            {
                val sculkShrieker = it.blockData as? SculkShrieker ?: return@BlockArgParseResult false

                (canSummon == null || sculkShrieker.isCanSummon == canSummon) &&
                        (shrieking == null || sculkShrieker.isShrieking == shrieking)
            },
            {
                val sculkShrieker = it.blockData as? SculkShrieker ?: return@BlockArgParseResult

                if (canSummon != null) sculkShrieker.isCanSummon = canSummon
                if (shrieking != null) sculkShrieker.isShrieking = shrieking

                it.blockData = sculkShrieker
            }
        )
    }
}