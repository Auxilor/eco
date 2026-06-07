package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.Tameable

object EntityArgParserTamed : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var tamed = false

        for (arg in args) {
            if (arg.equals("tamed", true)) {
                tamed = true
            }
        }

        if (!tamed) {
            return null
        }

        return EntityArgParseResult(
            {
                if (it !is Tameable) {
                    return@EntityArgParseResult false
                }

                it.isTamed
            },
            {
                (it as? Tameable)?.isTamed = true
            }
        )
    }
}
