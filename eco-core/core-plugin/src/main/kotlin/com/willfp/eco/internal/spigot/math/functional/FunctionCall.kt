package com.willfp.eco.internal.spigot.math.functional

import com.willfp.eco.internal.spigot.math.token.TokenType
import com.willfp.eco.internal.spigot.math.token.Value

class FunctionCall(
    private val function: Function,
    private val values: Array<Value>
) : Value {
    fun getFunction(): Function = function
    fun getValues(): Array<Value> = values

    override fun getType() = TokenType.FUNCTION_CALL

    override fun getValue(variableValues: DoubleArray): Double {
        val args = DoubleArray(values.size) { values[it].getValue(variableValues) }
        return function.call(args)
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