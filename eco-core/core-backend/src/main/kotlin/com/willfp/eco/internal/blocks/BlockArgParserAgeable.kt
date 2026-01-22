package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.Sapling

object BlockArgParserAgeable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        val maximumAge = when (blockData) {
            is Ageable -> blockData.maximumAge
            is Sapling -> blockData.maximumStage
            else -> return null
        }
        var age: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("age", true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            val argAge = argSplit[1].toIntOrNull() ?: continue
            if (argAge in (maximumAge + 1)..<0) {
                continue
            }
            age = argAge
        }

        age ?: return null

        return BlockArgParseResult(
            {
                when (val data = it.blockData) {
                    is Ageable -> data.age == age
                    is Sapling -> data.stage == age
                    else -> false
                }
            },
            {
                val data = it.blockData

                when (data) {
                    is Ageable -> data.age = age
                    is Sapling -> data.stage = age
                    else -> return@BlockArgParseResult
                }

                it.blockData = data
            }
        )
    }
}