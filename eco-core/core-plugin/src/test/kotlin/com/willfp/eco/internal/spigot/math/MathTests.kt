package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.exceptions.ExpressionCompilationException
import com.willfp.eco.internal.spigot.math.exceptions.ExpressionEvaluationException
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.function.ToDoubleFunction
import kotlin.math.max
import kotlin.math.min

internal class MathTests {

    @Test
    fun constantTest() {
        Assertions.assertEquals(Math.PI, eval("pi"), DELTA, "Pi equality")
        Assertions.assertEquals(Math.E, eval("e"), DELTA, "Euler's constant equality")
        Assertions.assertEquals(1.0, eval("true"), DELTA, "True equal to 1")
        Assertions.assertEquals(0.0, eval("false"), DELTA, "False equal to 0")
        Assertions.assertEquals(-1.0, eval("-1"), "Negation operator")
    }

    @Test
    fun basicOperationTest() {
        Assertions.assertEquals(2.0, eval("1+1"), "Simple addition")
        Assertions.assertEquals(2.0, eval("1 + 1"), "Simple expression with whitespace")
        Assertions.assertEquals(2.0, eval("            1      +       1       "), "Lots of whitespace")
        Assertions.assertEquals(8.0, eval("2^3"), "Simple exponent test")
        Assertions.assertEquals(10.0, eval("15 - 5"), "Simple subtraction test")
        Assertions.assertEquals(2.0, eval("1--1"), "Subtraction and negate operator")
        Assertions.assertEquals(2.0, eval("    1     --    1"), "Somewhat confusing whitespace")
        Assertions.assertEquals(5.0, eval("10 / 2"), "Asymmetric operator")
    }

    @Test
    fun complexOperationTest() {
        Assertions.assertEquals(9.0, eval("6/2*(1+2)"), "Order of operations")
        Assertions.assertEquals(5.0, eval("6/2*1+2"), "Order of operations 2")
        Assertions.assertEquals(1.0, eval("tan(atan(cos(acos(sin(asin(1))))))"), DELTA, "Trig functions")
        Assertions.assertEquals(
            402193.3186140596,
            eval("6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4"),
            DELTA,
            "Large expression"
        )
        Assertions.assertEquals(-5.0, eval("1-(2)*3"), DELTA, "Weird syntax")
        Assertions.assertEquals(1.0, eval("--1"), "Adjacent operators")
    }

    @Test
    fun booleanLogicTest() {
        Assertions.assertEquals(1.0, eval("true & true"), "Boolean and")
        Assertions.assertEquals(1.0, eval("true | false"), "Boolean or")
        Assertions.assertEquals(0.0, eval("true & (true & false | false)"), "More complex boolean expression")
        Assertions.assertEquals(1.0, eval("1 = 1 & 3 = 3"), "Arithmetic comparisons")
        Assertions.assertEquals(1.0, eval("1 != 2 & 3 != 4"), "Using !=")
    }

    @Test
    fun syntaxTest() {
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("(") },
            "Lone opening paren"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile(")") },
            "Lone closing paren"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("1 1") },
            "No operator"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("+") },
            "Only operator"
        )
    }

    @Test
    fun variableTest() {
        val env = ExpressionEnv()
        env.setVariableNames("x", "y")
        Assertions.assertEquals(33.0, compile("x * y", env).evaluate(11.0, 3.0), "Multiplying named variables")
        Assertions.assertThrows(
            ExpressionEvaluationException::class.java,
            { compile("x * y", env).evaluate(1.0) },
            "Too few values"
        )
        Assertions.assertThrows(
            ExpressionEvaluationException::class.java
        ) { compile("x", env).evaluate() }
    }

    @Test
    fun functionTest() {
        val env = ExpressionEnv()
        env.addFunction("mult", 2) { d: DoubleArray? -> d!![0] * d[1] }
        env.addFunction("four", 0) { _: DoubleArray? -> 4.0 }
        Assertions.assertEquals(45.0, compile("mult(15, 3)", env).evaluate(), "Basic function")
        Assertions.assertEquals(96.0, compile("mult(2, mult(4, mult(3, 4)))", env).evaluate(), "Nested functions")
        Assertions.assertEquals(4.0, compile("four()", env).evaluate(), "No-argument function")
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("mult", env) },
            "No argument list"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("mult(1)", env) },
            "Not enough arguments"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("mult()", env) },
            "Not enough arguments"
        )
        Assertions.assertThrows(
            ExpressionCompilationException::class.java,
            { compile("mult(1, 2, 3)", env) },
            "Too many arguments"
        )
    }

    @Test
    fun variableArgsFunctionTest() {
        val env = ExpressionEnv()
        env.addFunction("sum") { d: DoubleArray? ->
            var s = 0.0
            for (v in d!!) s += v
            s
        }
        env.addFunction("max", ToDoubleFunction { d: DoubleArray? ->
            if (d!!.isEmpty()) return@ToDoubleFunction 0.0
            var m = d[0]
            for (i in 1..<d.size) m = max(m, d[i])
            m
        })
        env.addFunction("min", ToDoubleFunction { d: DoubleArray? ->
            if (d!!.isEmpty()) return@ToDoubleFunction 0.0
            var m = d[0]
            for (i in 1..<d.size) m = min(m, d[i])
            m
        })
        env.addFunction("avg", ToDoubleFunction { d: DoubleArray? ->
            if (d!!.isEmpty()) return@ToDoubleFunction 0.0
            var s = 0.0
            for (v in d) s += v
            s / d.size
        })
        Assertions.assertEquals(0.0, compile("sum()", env).evaluate(), DELTA)
        Assertions.assertEquals(6.0, compile("sum(1, 2, 3)", env).evaluate(), DELTA)
        Assertions.assertEquals(10.0, compile("sum(1, 2, 3, 4)", env).evaluate(), DELTA)
        Assertions.assertEquals(-1.0, compile("sum(1, 2, -4)", env).evaluate(), DELTA)
        Assertions.assertEquals(5.0, compile("max(1, 5, 3)", env).evaluate(), DELTA)
        Assertions.assertEquals(2.0, compile("max(1, 2, -3)", env).evaluate(), DELTA)
        Assertions.assertEquals(-1.0, compile("max(-1, -2, -3)", env).evaluate(), DELTA)
        Assertions.assertEquals(-3.0, compile("max(-3)", env).evaluate(), DELTA)
        Assertions.assertEquals(
            83080.0,
            compile("max( 0.0, (378044 * 100 / 100.0 - 294964) * 1.0 ) - 0.0", env).evaluate(),
            DELTA
        )
        Assertions.assertEquals(0.0, compile("max()", env).evaluate(), DELTA)
        Assertions.assertEquals(1.0, compile("min(1, 5, 3)", env).evaluate(), DELTA)
        Assertions.assertEquals(-5.0, compile("min(-1, -5, 0)", env).evaluate(), DELTA)
        Assertions.assertEquals(-3.0, compile("min(-3)", env).evaluate(), DELTA)
        Assertions.assertEquals(0.0, compile("min()", env).evaluate(), DELTA)
        Assertions.assertEquals(2.5, compile("avg(1, 2, 3, 4)", env).evaluate(), DELTA)
        Assertions.assertEquals(-10.0, compile("avg(-10)", env).evaluate(), DELTA)
        Assertions.assertEquals(10.0, compile("avg(10)", env).evaluate(), DELTA)
        Assertions.assertEquals(0.0, compile("avg()", env).evaluate(), DELTA)
    }

    @Test
    fun rightAssociativityTest() {
        // ^ must be right-associative: 2^3^2 = 2^(3^2) = 2^9 = 512, not (2^3)^2 = 64
        Assertions.assertEquals(512.0, eval("2^3^2"), "Chained exponent right-associativity")
        Assertions.assertEquals(512.0, eval("2 ^ 3 ^ 2"), "Chained exponent right-associativity with spaces")
        Assertions.assertEquals(8.0, eval("2^3"), "Single exponent unaffected")
    }

    @Test
    fun leftAssociativityTest() {
        // All non-exponent operators must be left-associative
        Assertions.assertEquals(3.0, eval("10 - 4 - 3"), "Subtraction: (10-4)-3=3, not 10-(4-3)=9")
        Assertions.assertEquals(2.0, eval("16 / 4 / 2"), "Division: (16/4)/2=2, not 16/(4/2)=8")
        Assertions.assertEquals(2.0, eval("100 / 10 / 5"), "Chained division")
    }

    @Test
    fun rootingTest() {
        Assertions.assertEquals(2.0, eval("sqrt(4)"), "Square Rooting")
        Assertions.assertEquals(2.0, eval("cbrt(8)"), "Cube Rooting")
    }

    @Test
    fun scientificNotationTest() {
        Assertions.assertEquals(2E7, eval("2E7"), DELTA)
    }

    @Test
    fun noInlineRandomTest() {
        val expr: CompiledExpression = compile("rand1000000")
        Assertions.assertNotEquals(expr.evaluate(), expr.evaluate())
    }

    @Test
    fun inlineTest() {
        Assertions.assertEquals("6.0", compile("1 + 2 + 3").toString())
        Assertions.assertEquals("-1.0", compile("-1").toString())
        Assertions.assertEquals("1.0", compile("--1").toString())
    }

    @Test
    fun largeExpressionWithCustomFunctionTest() {
        val env = ExpressionEnv()
        env.addFunction("max", 2) { d: DoubleArray? -> max(d!![0], d[1]) }
        val expr = "max( 0.0, (378044 * 100 / 100.0 - 294964) * 1.0 ) - 0.0"
        val compiled: CompiledExpression = compile(expr, env)
        Assertions.assertEquals(83080.0, compiled.evaluate())
    }

    companion object {
        private const val DELTA = 1e-7

        private fun compile(expr: String): CompiledExpression = ExpressionParser(
            input = expr,
            environment = ExpressionEnv()
        ).parse()

        private fun compile(expr: String, env: ExpressionEnv): CompiledExpression = ExpressionParser(
            input = expr,
            environment = env
        ).parse()

        private fun eval(expr: String): Double = compile(expr = expr).evaluate()
    }

}