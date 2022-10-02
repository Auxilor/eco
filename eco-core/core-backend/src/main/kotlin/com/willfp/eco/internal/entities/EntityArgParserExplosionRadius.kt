package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.Creeper

object EntityArgParserExplosionRadius : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var size: Int? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("explosion-radius", ignoreCase = true)) {
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
                    is Creeper -> it.explosionRadius == size
                    else -> false
                }
            },
            {
                when (it) {
                    is Creeper -> it.explosionRadius = size
                }
            }
        )
    }
}