package com.willfp.eco.internal.blocks

import com.willfp.eco.core.blocks.args.BlockArgParseResult
import com.willfp.eco.core.blocks.args.BlockArgParser
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData

object BlockArgParserAgeable : BlockArgParser {
    override fun parseArguments(args: Array<out String>, blockData: BlockData): BlockArgParseResult? {
        var age: Int? = null

        val ageable = blockData as? Ageable ?: return null
        val maximumAge = ageable.maximumAge

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("age", ignoreCase = true)) {
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

        ageable.age = age

        return BlockArgParseResult(
            {
                val ageable = it.blockData as? Ageable ?: return@BlockArgParseResult false

                ageable.age == age
            },
            {
                val ageable = it.blockData as? Ageable ?: return@BlockArgParseResult

                ageable.age = age
                it.blockData = ageable
            }
        )
    }
}