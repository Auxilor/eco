package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Campfire

object BlockArgParserCampfire : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var signalFire: Boolean? = null

        val campfire = blockData as? Campfire ?: return null

        for (arg in args) {
            if (arg.equals("signal_fire", true)) {
                signalFire = true
            }
        }

        signalFire ?: return null

        campfire.isSignalFire = signalFire

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