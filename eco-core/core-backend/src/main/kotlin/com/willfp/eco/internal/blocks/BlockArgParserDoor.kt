package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Door

object BlockArgParserDoor : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var doorHinge: Door.Hinge? = null

        val door = blockData as? Door ?: return null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("hinge", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            doorHinge = runCatching { Door.Hinge.valueOf(argSplit[1].uppercase()) }.getOrNull() ?: continue
        }

        doorHinge ?: return null

        door.hinge = doorHinge

        return BlockArgParseResult(
            {
                val door = it.blockData as? Door ?: return@BlockArgParseResult false

                door.hinge == doorHinge
            },
            {
                val door = it.blockData as? Door ?: return@BlockArgParseResult

                door.hinge = doorHinge
                it.blockData = door
            }
        )
    }
}