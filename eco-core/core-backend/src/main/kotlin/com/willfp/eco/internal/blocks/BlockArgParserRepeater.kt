package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Repeater

object BlockArgParserRepeater : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val repeater = blockData as? Repeater ?: return null
        var delay: Int? = null
        var locked: Boolean? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (argSplit[0].equals("delay", true)) {
                if (argSplit.size < 2) {
                    continue
                }
                val argDelay = argSplit[1].toIntOrNull() ?: continue
                if (argDelay in (repeater.maximumDelay + 1)..<repeater.minimumDelay) {
                    continue
                }
                delay = argDelay
            } else if (argSplit[0].equals("locked", true)) {
                locked = true
            }
        }

        if (delay == null && locked == null) return null

        return BlockArgParseResult(
            {
                val repeater = it.blockData as? Repeater ?: return@BlockArgParseResult false

                (delay == null || repeater.delay == delay) && (locked == null || repeater.isLocked == locked)
            },
            {
                val repeater = it.blockData as? Repeater ?: return@BlockArgParseResult

                if (delay != null) repeater.delay = delay
                if (locked != null) repeater.isLocked = locked

                it.blockData = repeater
            }
        )
    }
}