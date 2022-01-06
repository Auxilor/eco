package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.LivingEntity

class EntityArgParserNoAI : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var noAI = false

        for (arg in args) {
            if (arg.equals("unbreakable", true)) {
                noAI = true
            }
        }

        if (!noAI) {
            return null
        }

        return EntityArgParseResult(
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult false
                }

                !it.hasAI()
            },
            {
                (it as? LivingEntity)?.setAI(false)
            }
        )
    }
}