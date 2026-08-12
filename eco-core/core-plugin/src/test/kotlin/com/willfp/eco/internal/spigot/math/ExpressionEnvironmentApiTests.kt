package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.math.ExpressionCompilationException
import com.willfp.eco.core.math.RandomSource
import com.willfp.eco.internal.spigot.math.api.EcoExpressionEnvironmentBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Random
import java.util.function.ToDoubleFunction

internal class ExpressionEnvironmentApiTests {

    @Test
    fun `compile and evaluate with variables`() {
        val env = builder().variables("x", "y").build()
        val expr = env.compileOrThrow("x * y + 1")
        Assertions.assertEquals(13.0, expr.evaluate(3.0, 4.0), DELTA)
        Assertions.assertEquals(2, expr.getVariableCount())
    }

    @Test
    fun `custom function is callable`() {
        val env = builder()
            .variables("x")
            .function("triple", 1, ToDoubleFunction { it[0] * 3 })
            .build()
        Assertions.assertEquals(21.0, env.compileOrThrow("triple(x)").evaluate(7.0), DELTA)
    }

    @Test
    fun `variadic custom function is callable`() {
        val env = builder()
            .function("total", ToDoubleFunction { args -> args.sum() })
            .build()
        Assertions.assertEquals(10.0, env.compileOrThrow("total(1, 2, 3, 4)").evaluate(), DELTA)
        Assertions.assertEquals(0.0, env.compileOrThrow("total()").evaluate(), DELTA)
    }

    @Test
    fun `variables called twice replaces the previous set`() {
        val env = builder().variables("a", "b", "c").variables("x").build()
        val expr = env.compileOrThrow("x * 2")
        Assertions.assertEquals(1, expr.getVariableCount())
        Assertions.assertEquals(8.0, expr.evaluate(4.0), DELTA)
        Assertions.assertNull(env.compile("a"), "Replaced variable must no longer resolve")
    }

    @Test
    fun `compile returns null on malformed input`() {
        val env = builder().build()
        Assertions.assertNull(env.compile("("))
        Assertions.assertNull(env.compile("1 1"))
        Assertions.assertNull(env.compile("+"))
        Assertions.assertNull(env.compile("unknownFn(1)"))
        Assertions.assertNull(env.compile("min()"))
    }

    @Test
    fun `compileOrThrow reports expression and position`() {
        val env = builder().build()
        val thrown = Assertions.assertThrows(ExpressionCompilationException::class.java) {
            env.compileOrThrow("1 + ")
        }
        Assertions.assertEquals("1 + ", thrown.expression)
        Assertions.assertTrue(thrown.position >= 0, "Position must be reported")
        Assertions.assertTrue(thrown.position <= "1 + ".length, "Position must be within bounds")
    }

    @Test
    fun `too few values throws IllegalArgumentException`() {
        val expr = builder().variables("x", "y").build().compileOrThrow("x + y")
        Assertions.assertThrows(IllegalArgumentException::class.java) { expr.evaluate(1.0) }
        Assertions.assertThrows(IllegalArgumentException::class.java) { expr.evaluate() }
    }

    @Test
    fun `determinism is reported`() {
        val env = builder()
            .function("pure", 1, ToDoubleFunction { it[0] })
            .function("impure", 1, ToDoubleFunction { it[0] }, false)
            .build()
        Assertions.assertTrue(env.compileOrThrow("pure(1) + 2").isDeterministic)
        Assertions.assertFalse(env.compileOrThrow("impure(1) + 2").isDeterministic)
        Assertions.assertFalse(env.compileOrThrow("rand10").isDeterministic)
        Assertions.assertFalse(env.compileOrThrow("random(1, 2)").isDeterministic)
    }

    @Test
    fun `seeded random source is reproducible`() {
        val expr = builder().build().compileOrThrow("random(0, 1000)")
        val a = expr.evaluate(seeded(99L))
        val b = expr.evaluate(seeded(99L))
        Assertions.assertEquals(a, b, 0.0)
    }

    @Test
    fun `name collision with built-in function is rejected`() {
        val thrown = Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().function("min", 2, ToDoubleFunction { it[0] }).build()
        }
        Assertions.assertTrue("min" in thrown.message!!, "Message must name the collision")
    }

    @Test
    fun `name collision with unary operator is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().function("sqrt", 1, ToDoubleFunction { it[0] }).build()
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().function("rand", 1, ToDoubleFunction { it[0] }).build()
        }
    }

    @Test
    fun `name collision with constant is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().function("pi", 0, ToDoubleFunction { 3.0 }).build()
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().function("true", 0, ToDoubleFunction { 1.0 }).build()
        }
    }

    @Test
    fun `name collision with declared variable is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder().variables("width").function("width", 0, ToDoubleFunction { 1.0 }).build()
        }
    }

    @Test
    fun `duplicate custom function name is rejected`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            builder()
                .function("dup", 1, ToDoubleFunction { it[0] })
                .function("dup", 2, ToDoubleFunction { it[0] })
                .build()
        }
    }

    @Test
    fun `custom function receives a pooled array`() {
        val seen = ArrayList<DoubleArray>()
        val env = builder().function("record", 1, ToDoubleFunction { args ->
            seen.add(args)
            args[0]
        }).build()
        val expr = env.compileOrThrow("record(1)")
        expr.evaluate()
        expr.evaluate()
        Assertions.assertSame(seen[0], seen[1])
    }

    companion object {
        private const val DELTA = 1e-9

        fun builder() = EcoExpressionEnvironmentBuilder()

        fun seeded(seed: Long): RandomSource {
            val random = Random(seed)
            return RandomSource { random.nextDouble() }
        }
    }
}
