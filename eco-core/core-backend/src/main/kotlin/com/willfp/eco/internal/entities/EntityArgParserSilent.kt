package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.args.EntityArgParseResult
import com.willfp.eco.core.entities.args.EntityArgParser

object EntityArgParserSilent : EntityArgParser {
    override fun parseArguments(args: Array<out String>): EntityArgParseResult? {
        var silent = false

        for (arg in args) {
            if (arg.equals("silent", true)) {
                silent = true
            }
        }

        if (!silent) {
            return null
        }

        return EntityArgParseResult(
            {
                it.isSilent
            },
            {
                it.isSilent = true
            }
        )
    }
}