package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser
import com.willfp.eco.util.StringUtils

object EntityArgParserName : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var name: String? = null

        for (arg in args) {
            if (!arg.lowercase().startsWith("name:")) {
                continue
            }
            name = arg.substring(5, arg.length)
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