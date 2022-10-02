package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

object EntityArgParserHealth : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var health: Double? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("health", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            health = argSplit[1].toDoubleOrNull()
        }

        health ?: return null

        return EntityArgParseResult(
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult false
                }

                it.health >= health
            },
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult
                }

                it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = health
                it.health = health
            }
        )
    }
}