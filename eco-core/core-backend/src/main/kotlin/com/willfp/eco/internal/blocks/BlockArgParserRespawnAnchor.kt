package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.RespawnAnchor

object BlockArgParserRespawnAnchor : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val respawnAnchor = blockData as? RespawnAnchor ?: return null
        var charges: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("charges", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argCharges = argSplit[1].toIntOrNull() ?: continue
            if (argCharges in (respawnAnchor.maximumCharges + 1)..<0) {
                continue
            }
            charges = argCharges
        }

        charges ?: return null

        return BlockArgParseResult(
            {
                val respawnAnchor = it.blockData as? RespawnAnchor ?: return@BlockArgParseResult false

                respawnAnchor.charges == charges
            },
            {
                val respawnAnchor = it.blockData as? RespawnAnchor ?: return@BlockArgParseResult

                respawnAnchor.charges = charges
                it.blockData = respawnAnchor
            }
        )
    }
}