package redempt.crunch.exceptions

import redempt.crunch.ExpressionParser

class ExpressionCompilationException(
    private val parser: ExpressionParser?,
    message: String
) : RuntimeException(generateMessage(parser, message)) {
    fun getParser(): ExpressionParser? = parser

    companion object {
        private fun generateMessage(parser: ExpressionParser?, message: String): String {
            if (parser == null) return message
            return "$message:\n${parser.input}\n${" ".repeat(parser.cursor)}^"
        }
    }
}