package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ArgumentPoolingTests {

    @Test
    fun `argument array is reused across calls`() {
        val seen = ArrayList<DoubleArray>()
        val env = ExpressionEnv()
        env.addFunction("record", 2) { args ->
            seen.add(args)
            args[0] + args[1]
        }

        val expr = ExpressionParser("record(1, 2)", env).parse()
        expr.evaluate()
        expr.evaluate()
        expr.evaluate()

        Assertions.assertEquals(3, seen.size)
        Assertions.assertSame(seen[0], seen[1], "Pooled array must be the same instance")
        Assertions.assertSame(seen[1], seen[2], "Pooled array must be the same instance")
    }

    @Test
    fun `nested same-arity calls do not corrupt each other`() {
        val env = ExpressionEnv()
        env.addFunction("mult", 2) { it[0] * it[1] }
        Assertions.assertEquals(
            96.0,
            ExpressionParser("mult(2, mult(4, mult(3, 4)))", env).parse().evaluate()
        )
    }

    @Test
    fun `different arities get different arrays`() {
        val env = ExpressionEnv()
        env.addFunction("two", 2) { it.size.toDouble() }
        env.addFunction("three", 3) { it.size.toDouble() }
        Assertions.assertEquals(2.0, ExpressionParser("two(1, 2)", env).parse().evaluate())
        Assertions.assertEquals(3.0, ExpressionParser("three(1, 2, 3)", env).parse().evaluate())
    }

    @Test
    fun `zero-argument function still works`() {
        val env = ExpressionEnv()
        env.addFunction("four", 0) { 4.0 }
        Assertions.assertEquals(4.0, ExpressionParser("four()", env).parse().evaluate())
    }
}
