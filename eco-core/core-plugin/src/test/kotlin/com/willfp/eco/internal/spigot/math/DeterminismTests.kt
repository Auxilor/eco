package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import com.willfp.eco.internal.spigot.math.functional.Function
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.OptionalInt

internal class DeterminismTests {

    @Test
    fun `plain arithmetic is deterministic`() {
        Assertions.assertTrue(compile("1 + 2 * 3").isDeterministic())
        Assertions.assertTrue(compile("sqrt(16) + abs(-3)").isDeterministic())
        Assertions.assertTrue(compile("min(1, 2)").isDeterministic())
    }

    @Test
    fun `rand is non-deterministic`() {
        Assertions.assertFalse(compile("rand10").isDeterministic())
        Assertions.assertFalse(compile("rand10 + 5").isDeterministic())
        Assertions.assertFalse(compile("sqrt(rand100)").isDeterministic())
    }

    @Test
    fun `random is non-deterministic`() {
        Assertions.assertFalse(compile("random(1, 2)").isDeterministic())
        Assertions.assertFalse(compile("random(1, 2) * 0").isDeterministic())
    }

    @Test
    fun `non-deterministic custom function taints the expression`() {
        val env = ExpressionEnv()
        env.addFunction(Function("noisy", OptionalInt.of(1), deterministic = false) { it[0] })
        env.addFunction(Function("quiet", OptionalInt.of(1), deterministic = true) { it[0] })

        Assertions.assertFalse(compile("noisy(1)", env).isDeterministic())
        Assertions.assertFalse(compile("quiet(noisy(1))", env).isDeterministic())
        Assertions.assertFalse(compile("1 + noisy(1)", env).isDeterministic())
        Assertions.assertTrue(compile("quiet(1)", env).isDeterministic())
    }

    @Test
    fun `fully folded constant expression is deterministic`() {
        // 2 + 3 * 4 folds to a LiteralValue at parse time and has no bytecode at all.
        Assertions.assertTrue(compile("2 + 3 * 4").isDeterministic())
        Assertions.assertTrue(compile("pi").isDeterministic())
    }

    @Test
    fun `variables do not make an expression non-deterministic`() {
        val env = ExpressionEnv()
        env.setVariableNames("x")
        Assertions.assertTrue(compile("x * 2", env).isDeterministic())
    }

    companion object {
        private fun compile(expr: String, env: ExpressionEnv = ExpressionEnv()): CompiledExpression =
            ExpressionParser(expr, env).parse()
    }
}
