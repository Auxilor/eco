package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.exceptions.ExpressionEvaluationException
import com.willfp.eco.internal.spigot.math.token.Constant
import com.willfp.eco.internal.spigot.math.token.LiteralValue
import com.willfp.eco.internal.spigot.math.token.Value

class CompiledExpression(
    private val value: Value,
    private val variableCount: Int
) {
    private val isConstant: Boolean = value is LiteralValue || value is Constant
    private val constantValue: Double = if (isConstant) value.getValue(EMPTY_VARS) else 0.0
    private val bytecode: BytecodeExpression? = if (!isConstant) BytecodeExpression.compile(value) else null

    fun evaluate(vararg values: Double): Double {
        if (values.size < variableCount) {
            throw ExpressionEvaluationException("Expected $variableCount variable values but got ${values.size}")
        }
        if (isConstant) return constantValue
        return bytecode!!.evaluate(values)
    }

    fun evaluate(): Double {
        if (variableCount > 0) {
            throw ExpressionEvaluationException("Expected $variableCount variable values")
        }
        if (isConstant) return constantValue
        return bytecode!!.evaluate(EMPTY_VARS)
    }

    fun clone(): CompiledExpression = CompiledExpression(value, variableCount)

    override fun toString() = value.toString()

    companion object {
        private val EMPTY_VARS = DoubleArray(0)
    }
}
