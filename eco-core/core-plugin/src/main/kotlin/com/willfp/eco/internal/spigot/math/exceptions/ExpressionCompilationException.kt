package com.willfp.eco.internal.spigot.math.exceptions

import com.willfp.eco.internal.spigot.math.ExpressionParser

class ExpressionCompilationException(
    parser: ExpressionParser?,
    message: String
): RuntimeException(generateMessage(parser, message)) {

    companion object {
        private fun generateMessage(parser: ExpressionParser?, message: String): String =
            if (parser == null) {
                message
            } else {
                "$message:\n${parser.input}\n${" ".repeat(parser.cursor)}^"
            }
    }

}