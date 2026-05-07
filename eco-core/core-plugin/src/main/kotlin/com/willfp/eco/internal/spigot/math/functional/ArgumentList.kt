package redempt.crunch.functional

import redempt.crunch.token.Token
import redempt.crunch.token.TokenType
import redempt.crunch.token.Value

class ArgumentList(private val arguments: Array<Value>) : Token {
    override fun getType() = TokenType.ARGUMENT_LIST
    fun getArguments() = arguments
}