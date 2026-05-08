package com.willfp.eco.internal.particle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParticleExpressionTest {

    @Test
    fun `Constant returns fixed value regardless of input array`() {
        val expr = ParticleExpression.Constant(7.5)
        assertEquals(7.5, expr.evaluate(DoubleArray(0)))
        assertEquals(7.5, expr.evaluate(doubleArrayOf(1.0, 2.0, 3.0)))
    }

    @Test
    fun `Variable calls lambda with full values array`() {
        val expr = ParticleExpression.Variable({ values -> values[0] + values[1] }, listOf("a", "b"))
        assertEquals(5.0, expr.evaluate(doubleArrayOf(2.0, 3.0)))
    }

    @Test
    fun `Variable uses correct index from values array`() {
        val expr = ParticleExpression.Variable({ values -> values[2] }, listOf("x", "y", "z"))
        assertEquals(9.0, expr.evaluate(doubleArrayOf(1.0, 2.0, 9.0)))
    }

    @Test
    fun `Variable varNames are stored correctly`() {
        val names = listOf("t", "i", "n")
        val expr = ParticleExpression.Variable({ 0.0 }, names)
        assertEquals(names, expr.varNames)
    }
}
