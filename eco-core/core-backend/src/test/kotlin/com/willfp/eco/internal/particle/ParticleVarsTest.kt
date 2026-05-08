package com.willfp.eco.internal.particle

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParticleVarsTest {

    private val baseScope = EvaluationScope.empty(PlaceholderContext.EMPTY)
        .withReserved(mapOf("t" to 2.0))

    @Test
    fun `EMPTY vars returns scope with same lookup values`() {
        val result = ParticleVars.EMPTY.applyTo(baseScope)
        assertEquals(2.0, result.lookup("t"))
    }

    @Test
    fun `single var entry is accessible after apply`() {
        val vars = ParticleVars(
            listOf(
                ParticleVars.Entry("myVar", ParticleExpression.Constant(7.0), emptyList())
            )
        )
        val result = vars.applyTo(baseScope)
        assertEquals(7.0, result.lookup("myVar"))
    }

    @Test
    fun `later var can reference earlier var via its value`() {
        // first var = 3.0, second var = first * 2 = 6.0
        val vars = ParticleVars(
            listOf(
                ParticleVars.Entry("a", ParticleExpression.Constant(3.0), emptyList()),
                ParticleVars.Entry("b", ParticleExpression.Variable({ v -> v[0] * 2 }, listOf("a")), listOf("a"))
            )
        )
        val result = vars.applyTo(baseScope)
        assertEquals(3.0, result.lookup("a"))
        assertEquals(6.0, result.lookup("b"))
    }

    @Test
    fun `var can reference reserved variable`() {
        // t = 2.0 from baseScope; doubled = t * 2 = 4.0
        val vars = ParticleVars(
            listOf(
                ParticleVars.Entry("doubled", ParticleExpression.Variable({ v -> v[0] * 2 }, listOf("t")), listOf("t"))
            )
        )
        val result = vars.applyTo(baseScope)
        assertEquals(4.0, result.lookup("doubled"))
    }
}
