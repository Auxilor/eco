package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.Creeper

object EntityArgParserCharged : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var noAI = false

        for (arg in args) {
            if (arg.equals("charged", true)) {
                noAI = true
            }
        }

        if (!noAI) {
            return null
        }

        return EntityArgParseResult(
            {
                if (it !is Creeper) {
                    return@EntityArgParseResult false
                }

                it.isPowered
            },
            {
                (it as? Creeper)?.isPowered = true
            }
        )
    }
}