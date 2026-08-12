package com.willfp.eco.internal.spigot.math.api

import com.willfp.eco.core.math.CompiledExpression
import com.willfp.eco.core.math.RandomSource
import com.willfp.eco.internal.spigot.math.CompiledExpression as InternalCompiledExpression

/**
 * Adapts the internal [InternalCompiledExpression] to the public [CompiledExpression] contract.
 *
 * The only behavioural difference is the exception type on too-few values: the public API throws
 * [IllegalArgumentException] rather than the internal `ExpressionEvaluationException`.
 */
class EcoCompiledExpression(
    private val handle: InternalCompiledExpression
) : CompiledExpression {

    override fun evaluate(vararg values: Double): Double {
        requireEnough(values.size)
        return handle.evaluate(*values)
    }

    override fun evaluate(random: RandomSource, vararg values: Double): Double {
        requireEnough(values.size)
        return handle.evaluate(random, *values)
    }

    override fun getVariableCount(): Int = handle.getVariableCount()

    override fun isDeterministic(): Boolean = handle.isDeterministic()

    private fun requireEnough(given: Int) {
        val required = handle.getVariableCount()
        require(given >= required) {
            "Expected $required variable values but got $given"
        }
    }

    override fun toString(): String = handle.toString()
}
