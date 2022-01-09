package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.util.StringUtils

class EntityArgParserName : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var name: String? = null

        for (arg in args) {
            val argSplit = arg.split(":")
            if (!argSplit[0].equals("name", ignoreCase = true)) {
                continue
            }
            if (argSplit.size < 2) {
                continue
            }
            name = argSplit[1]
        }

        name ?: return null

        val formatted = StringUtils.format(name)

        return EntityArgParseResult(
            { it.customName == formatted },
            {
                it.isCustomNameVisible = true
                it.customName = formatted
            }
        )
    }
}