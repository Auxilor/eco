package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.math.RandomSource
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Random

internal class RandomSourceTests {

    @Test
    fun `rand draws from the supplied source`() {
        val expr = compile("rand10")
        // A source that always returns 0.5 must make rand10 exactly 5.0.
        Assertions.assertEquals(5.0, expr.evaluate(constantSource(0.5)), DELTA)
        Assertions.assertEquals(0.0, expr.evaluate(constantSource(0.0)), DELTA)
    }

    @Test
    fun `seeded source is reproducible across runs`() {
        val expr = compile("rand100 + rand100")
        val first = expr.evaluate(seededSource(42L))
        val second = expr.evaluate(seededSource(42L))
        Assertions.assertEquals(first, second, 0.0, "Same seed must give the same result")
    }

    @Test
    fun `default overload still varies`() {
        val expr = compile("rand1000000")
        Assertions.assertNotEquals(expr.evaluate(), expr.evaluate())
    }

    @Test
    fun `random draws from the supplied source`() {
        val expr = compile("random(10, 20)")
        Assertions.assertEquals(15.0, expr.evaluate(constantSource(0.5)), DELTA)
        Assertions.assertEquals(10.0, expr.evaluate(constantSource(0.0)), DELTA)
    }

    @Test
    fun `random orders its bounds`() {
        val expr = compile("random(20, 10)")
        Assertions.assertEquals(15.0, expr.evaluate(constantSource(0.5)), DELTA)
    }

    @Test
    fun `random collapses near-equal bounds to the minimum`() {
        val expr = compile("random(5, 5)")
        Assertions.assertEquals(5.0, expr.evaluate(constantSource(0.9)), DELTA)
    }

    @Test
    fun `random is seeded reproducibly`() {
        val expr = compile("random(0, 1000) + random(0, 1000)")
        Assertions.assertEquals(
            expr.evaluate(seededSource(7L)),
            expr.evaluate(seededSource(7L)),
            0.0
        )
    }

    companion object {
        private const val DELTA = 1e-9

        fun compile(expr: String): CompiledExpression =
            ExpressionParser(expr, ExpressionEnv()).parse()

        fun constantSource(value: Double): RandomSource = RandomSource { value }

        fun seededSource(seed: Long): RandomSource {
            val random = Random(seed)
            return RandomSource { random.nextDouble() }
        }
    }
}
