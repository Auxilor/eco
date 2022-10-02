package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

object EntityArgParserJumpStrength : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var attributeValue: Double? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("jump-strength", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            attributeValue = argSplit[1].toDoubleOrNull()
        }

        attributeValue ?: return null

        return EntityArgParseResult(
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult false
                }

                val inst = it.getAttribute(Attribute.HORSE_JUMP_STRENGTH) ?: return@EntityArgParseResult false
                inst.value >= attributeValue
            },
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult
                }

                it.getAttribute(Attribute.HORSE_JUMP_STRENGTH)?.baseValue = attributeValue
            }
        )
    }
}