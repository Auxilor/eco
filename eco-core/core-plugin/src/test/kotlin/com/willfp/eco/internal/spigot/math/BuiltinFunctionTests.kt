package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.exceptions.ExpressionCompilationException
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BuiltinFunctionTests {

    @Test
    fun clampTest() {
        Assertions.assertEquals(5.0, eval("clamp(5, 0, 10)"), DELTA)
        Assertions.assertEquals(0.0, eval("clamp(-5, 0, 10)"), DELTA)
        Assertions.assertEquals(10.0, eval("clamp(50, 0, 10)"), DELTA)
        Assertions.assertEquals(0.0, eval("clamp(0, 0, 10)"), DELTA, "Lower boundary")
        Assertions.assertEquals(10.0, eval("clamp(10, 0, 10)"), DELTA, "Upper boundary")
        // max(lo, min(hi, v)) with lo > hi yields lo.
        Assertions.assertEquals(10.0, eval("clamp(5, 10, 0)"), DELTA, "Inverted bounds yield lo")
    }

    @Test
    fun lerpTest() {
        Assertions.assertEquals(0.0, eval("lerp(0, 10, 0)"), DELTA)
        Assertions.assertEquals(10.0, eval("lerp(0, 10, 1)"), DELTA)
        Assertions.assertEquals(5.0, eval("lerp(0, 10, 0.5)"), DELTA)
        Assertions.assertEquals(20.0, eval("lerp(0, 10, 2)"), DELTA, "Unclamped above")
        Assertions.assertEquals(-10.0, eval("lerp(0, 10, -1)"), DELTA, "Unclamped below")
    }

    @Test
    fun smoothstepTest() {
        Assertions.assertEquals(0.0, eval("smoothstep(0, 1, -5)"), DELTA, "Clamped below")
        Assertions.assertEquals(1.0, eval("smoothstep(0, 1, 5)"), DELTA, "Clamped above")
        Assertions.assertEquals(0.0, eval("smoothstep(0, 1, 0)"), DELTA)
        Assertions.assertEquals(1.0, eval("smoothstep(0, 1, 1)"), DELTA)
        Assertions.assertEquals(0.5, eval("smoothstep(0, 1, 0.5)"), DELTA)
        Assertions.assertEquals(0.15625, eval("smoothstep(0, 1, 0.25)"), DELTA)
    }

    @Test
    fun remapTest() {
        Assertions.assertEquals(50.0, eval("remap(0.5, 0, 1, 0, 100)"), DELTA)
        Assertions.assertEquals(0.0, eval("remap(0, 0, 1, 0, 100)"), DELTA)
        Assertions.assertEquals(100.0, eval("remap(1, 0, 1, 0, 100)"), DELTA)
        Assertions.assertEquals(200.0, eval("remap(2, 0, 1, 0, 100)"), DELTA, "Unclamped")
        Assertions.assertEquals(50.0, eval("remap(0.5, 1, 0, 100, 0)"), DELTA, "Inverted ranges")
    }

    @Test
    fun coreMathTest() {
        Assertions.assertEquals(1.0, eval("exp(0)"), DELTA)
        Assertions.assertEquals(Math.E, eval("exp(1)"), DELTA)
        Assertions.assertEquals(2.0, eval("log10(100)"), DELTA)
        Assertions.assertEquals(3.0, eval("log2(8)"), DELTA)
        Assertions.assertEquals(0.0, eval("log(1)"), DELTA, "log remains natural")
        Assertions.assertEquals(1.0, eval("sign(7)"), DELTA)
        Assertions.assertEquals(-1.0, eval("sign(-7)"), DELTA)
        Assertions.assertEquals(0.0, eval("sign(0)"), DELTA)
        Assertions.assertEquals(2.0, eval("trunc(2.9)"), DELTA)
        Assertions.assertEquals(-2.0, eval("trunc(-2.9)"), DELTA, "Toward zero")
        Assertions.assertEquals(0.0, eval("trunc(0.9)"), DELTA)
        Assertions.assertEquals(Math.atan2(1.0, 1.0), eval("atan2(1, 1)"), DELTA)
        Assertions.assertEquals(5.0, eval("hypot(3, 4)"), DELTA)
        Assertions.assertEquals(8.0, eval("pow(2, 3)"), DELTA)
        Assertions.assertEquals(eval("2 ^ 3"), eval("pow(2, 3)"), DELTA, "pow matches ^")
    }

    @Test
    fun nonFiniteTest() {
        Assertions.assertTrue(eval("sign(0 / 0)").isNaN(), "sign(NaN) is NaN")
        Assertions.assertTrue(eval("trunc(0 / 0)").isNaN(), "trunc(NaN) is NaN")
        Assertions.assertTrue(eval("clamp(0 / 0, 0, 1)").isNaN(), "clamp propagates NaN")
        Assertions.assertEquals(
            Double.POSITIVE_INFINITY,
            eval("hypot(1 / 0, 1)"),
            "hypot propagates infinity"
        )
        Assertions.assertEquals(0.0, eval("exp(-1 / 0)"), DELTA, "exp(-inf) is 0")
    }

    @Test
    fun arityErrorTest() {
        Assertions.assertThrows(ExpressionCompilationException::class.java) { compile("clamp(1, 2)") }
        Assertions.assertThrows(ExpressionCompilationException::class.java) { compile("lerp(1)") }
        Assertions.assertThrows(ExpressionCompilationException::class.java) { compile("remap(1, 2, 3, 4)") }
        Assertions.assertThrows(ExpressionCompilationException::class.java) { compile("exp(1, 2)") }
    }

    @Test
    fun namePrefixTest() {
        // These must not be mis-parsed by the longest-match CharTree.
        Assertions.assertEquals(Math.E, eval("e"), DELTA, "Constant e survives exp")
        Assertions.assertEquals(0.0, eval("log(1)"), DELTA, "log survives log10 and log2")
        Assertions.assertEquals(Math.atan(1.0), eval("atan(1)"), DELTA, "atan survives atan2")
        Assertions.assertEquals(Math.sin(1.0), eval("sin(1)"), DELTA, "sin survives sign")
        Assertions.assertEquals(Math.tan(1.0), eval("tan(1)"), DELTA, "tan survives trunc")
        Assertions.assertEquals(2.0, eval("sqrt(4)"), DELTA, "sqrt survives step and smoothstep")
        Assertions.assertEquals(2.0, eval("cbrt(8)"), DELTA, "cbrt survives clamp")
        Assertions.assertEquals(2.0, eval("ceil(1.5)"), DELTA, "ceil survives clamp")
    }

    @Test
    fun ifTest() {
        Assertions.assertEquals(1.0, eval("if(true, 1, 2)"), DELTA)
        Assertions.assertEquals(2.0, eval("if(false, 1, 2)"), DELTA)
        Assertions.assertEquals(1.0, eval("if(1, 1, 2)"), DELTA)
        Assertions.assertEquals(2.0, eval("if(0, 1, 2)"), DELTA)
        // Any non-zero condition is true, unlike !, & and | which test against exactly 1.0.
        Assertions.assertEquals(1.0, eval("if(7, 1, 2)"), DELTA, "Arbitrary non-zero is true")
        Assertions.assertEquals(1.0, eval("if(-3, 1, 2)"), DELTA, "Negative is true")
        Assertions.assertEquals(1.0, eval("if(0.5, 1, 2)"), DELTA, "Fractional is true")
        Assertions.assertEquals(5.0, eval("if(3 > 2, 5, 10)"), DELTA, "With a comparison")
    }

    @Test
    fun `if evaluates both branches eagerly`() {
        val seen = ArrayList<String>()
        val env = ExpressionEnv()
        env.addFunction("markA", 0) { seen.add("A"); 1.0 }
        env.addFunction("markB", 0) { seen.add("B"); 2.0 }

        val result = ExpressionParser("if(true, markA(), markB())", env).parse().evaluate()

        Assertions.assertEquals(1.0, result, DELTA)
        Assertions.assertTrue("A" in seen, "Taken branch evaluated")
        Assertions.assertTrue("B" in seen, "Untaken branch also evaluated - if is not lazy")
    }

    @Test
    fun `if untaken branch may be non-finite without throwing`() {
        Assertions.assertEquals(5.0, eval("if(true, 5, 1 / 0)"), DELTA)
    }

    @Test
    fun stepTest() {
        Assertions.assertEquals(0.0, eval("step(5, 4)"), DELTA)
        Assertions.assertEquals(1.0, eval("step(5, 5)"), DELTA, "Edge is inclusive")
        Assertions.assertEquals(1.0, eval("step(5, 6)"), DELTA)
        Assertions.assertEquals(1.0, eval("step(0, 0)"), DELTA)
        Assertions.assertEquals(0.0, eval("step(0, -1)"), DELTA)
    }

    @Test
    fun variadicMinMaxTest() {
        Assertions.assertEquals(3.0, eval("min(3)"), DELTA, "Arity 1")
        Assertions.assertEquals(3.0, eval("min(3, 7)"), DELTA, "Arity 2")
        Assertions.assertEquals(1.0, eval("min(5, 1, 9, 4, 7)"), DELTA, "Arity 5")
        Assertions.assertEquals(-3.0, eval("min(-3)"), DELTA)

        Assertions.assertEquals(3.0, eval("max(3)"), DELTA, "Arity 1")
        Assertions.assertEquals(7.0, eval("max(3, 7)"), DELTA, "Arity 2")
        Assertions.assertEquals(9.0, eval("max(5, 1, 9, 4, 7)"), DELTA, "Arity 5")
        Assertions.assertEquals(-1.0, eval("max(-1, -2, -3)"), DELTA)

        Assertions.assertEquals(4.0, eval("min(max(2, 4), 8)"), DELTA, "Nested")
    }

    @Test
    fun variadicZeroArityIsCompilationErrorTest() {
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("min()") },
            "min() must not compile"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("max()") },
            "max() must not compile"
        )
    }

    companion object {
        private const val DELTA = 1e-9

        fun compile(expr: String): CompiledExpression =
            ExpressionParser(expr, ExpressionEnv()).parse()

        fun eval(expr: String): Double = compile(expr).evaluate()
    }
}
