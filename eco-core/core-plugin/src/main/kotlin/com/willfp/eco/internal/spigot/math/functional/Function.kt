package redempt.crunch.functional

import redempt.crunch.token.Token
import redempt.crunch.token.TokenType
import java.util.OptionalInt
import java.util.function.ToDoubleFunction

class Function(
    private val name: String,
    private val argCount: OptionalInt,
    private val function: ToDoubleFunction<DoubleArray>
) : Token {
    override fun getType() = TokenType.FUNCTION
    fun getName() = name
    fun getArgCount() = argCount
    fun call(values: DoubleArray) = function.applyAsDouble(values)
}