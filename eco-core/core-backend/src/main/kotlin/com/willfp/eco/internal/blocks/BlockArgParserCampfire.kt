package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Campfire

object BlockArgParserCampfire : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        if (blockData !is Campfire) return null
        var signalFire: Boolean? = null

        for (arg in args) {
            if (arg.equals("signal_fire", true)) {
                signalFire = true
            }
        }

        signalFire ?: return null

        return BlockArgParseResult(
            {
                val campfire = it.blockData as? Campfire ?: return@BlockArgParseResult false

                campfire.isSignalFire == signalFire
            },
            {
                val campfire = it.blockData as? Campfire ?: return@BlockArgParseResult

                campfire.isSignalFire = signalFire
                it.blockData = campfire
            }
        )
    }
}