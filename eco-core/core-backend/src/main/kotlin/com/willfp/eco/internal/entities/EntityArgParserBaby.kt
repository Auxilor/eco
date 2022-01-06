package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.entity.Animals

class EntityArgParserBaby : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var baby = false

        for (arg in args) {
            if (arg.equals("baby", true)) {
                baby = true
            }
        }

        if (!baby) {
            return null
        }

        return EntityArgParseResult(
            {
                if (it !is Animals) {
                    return@EntityArgParseResult false
                }

                !it.isAdult
            },
            {
                (it as? Animals)?.setBaby()
            }
        )
    }
}