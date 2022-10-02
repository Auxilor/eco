package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.Phantom
import org.bukkit.entity.Slime

object EntityArgParserSize : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var size: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("size", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            size = argSplit[1].toIntOrNull()
        }

        size ?: return null

        return EntityArgParseResult(
            {
                when (it) {
                    is Slime -> it.size == size
                    is Phantom -> it.size == size
                    else -> false
                }
            },
            {
                when (it) {
                    is Slime -> it.size = size
                    is Phantom -> it.size = size
                }
            }
        )
    }
}