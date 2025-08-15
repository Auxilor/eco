package com.willfp.eco.internal.compat.modern.entities.parsers

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

object EntityArgParserScale : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var attributeValue: Double? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("scale", ignoreCase = true)) {
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

                val inst = it.getAttribute(Attribute.GENERIC_SCALE) ?: return@EntityArgParseResult false
                inst.value >= attributeValue
            },
            {
                if (it !is LivingEntity) {
                    return@EntityArgParseResult
                }

                it.getAttribute(Attribute.GENERIC_SCALE)?.baseValue = attributeValue
            }
        )
    }
}
