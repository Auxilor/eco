package com.willfp.eco.internal.particle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ParticleExpressionCompilerTest {

    @Test
    fun `compile delegates to impl`() {
        ParticleExpressionCompiler.setImpl { raw, _ ->
            ParticleExpression.Constant(raw.toDouble())
        }
        val result = ParticleExpressionCompiler.compile("3.14", emptyList())
        assertEquals(3.14, (result as ParticleExpression.Constant).value)
    }

    @Test
    fun `compile passes varNames to impl`() {
        val captured = mutableListOf<String>()
        ParticleExpressionCompiler.setImpl { _, varNames ->
            captured.addAll(varNames)
            ParticleExpression.Constant(0.0)
        }
        ParticleExpressionCompiler.compile("0", listOf("t", "i", "n"))
        assertEquals(listOf("t", "i", "n"), captured)
    }

    @Test
    fun `setImpl can be replaced`() {
        ParticleExpressionCompiler.setImpl { _, _ -> ParticleExpression.Constant(1.0) }
        ParticleExpressionCompiler.setImpl { _, _ -> ParticleExpression.Constant(2.0) }
        val result = ParticleExpressionCompiler.compile("anything", emptyList())
        assertEquals(2.0, (result as ParticleExpression.Constant).value)
    }

    @Test
    fun `compile throws when impl is not set`() {
        // Reset impl via reflection to test the uninitialised guard
        val field = ParticleExpressionCompiler::class.java.getDeclaredField("impl")
        field.isAccessible = true
        val previous = field.get(ParticleExpressionCompiler)
        field.set(ParticleExpressionCompiler, null)
        try {
            assertThrows(IllegalStateException::class.java) {
                ParticleExpressionCompiler.compile("1+1", emptyList())
            }
        } finally {
            field.set(ParticleExpressionCompiler, previous)
        }
    }
}
