package redempt.crunch.functional

import redempt.crunch.token.TokenType
import redempt.crunch.token.Value

class FunctionCall(
    private val function: Function,
    private val values: Array<Value>
) : Value {
    private val numbers = DoubleArray(values.size)

    fun getFunction(): Function = function
    fun getValues(): Array<Value> = values

    override fun getType() = TokenType.FUNCTION_CALL

    override fun getValue(variableValues: DoubleArray): Double {
        for (i in values.indices) {
            numbers[i] = values[i].getValue(variableValues)
        }
        return function.call(numbers)
    }

    override fun getClone(): Value = FunctionCall(function, values)

    override fun toString(): String {
        val sb = StringBuilder(function.getName()).append('(')
        for (i in values.indices) {
            sb.append(values[i])
            if (i != values.size - 1) sb.append(", ")
        }
        return sb.append(')').toString()
    }
}