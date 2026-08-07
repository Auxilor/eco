package com.willfp.eco.internal.spigot.math

import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Every expression here must produce a bit-identical result before and after the
 * mathematics upgrade. Covers every operator, constant and built-in that existed
 * at baseline a745df55, plus the built-ins added by the upgrade.
 */
internal class GoldenCorpusTests {

    @Test
    fun `golden corpus is bit-identical`() {
        for ((expression, expected) in CORPUS) {
            val actual = ExpressionParser(expression, ExpressionEnv()).parse().evaluate()
            Assertions.assertEquals(
                java.lang.Double.doubleToLongBits(expected),
                java.lang.Double.doubleToLongBits(actual),
                "Expression `$expression` expected $expected but got $actual"
            )
        }
    }

    @Test
    fun `golden corpus with variables is bit-identical`() {
        val env = ExpressionEnv()
        env.setVariableNames("x", "y")
        for ((expression, expected) in VARIABLE_CORPUS) {
            val actual = ExpressionParser(expression, env).parse().evaluate(3.0, 4.0)
            Assertions.assertEquals(
                java.lang.Double.doubleToLongBits(expected),
                java.lang.Double.doubleToLongBits(actual),
                "Expression `$expression` expected $expected but got $actual"
            )
        }
    }

    companion object {
        private val CORPUS: List<Pair<String, Double>> = listOf(
            // Constants
            "pi" to Math.PI,
            "e" to Math.E,
            "true" to 1.0,
            "false" to 0.0,

            // Binary operators
            "1 + 1" to 2.0,
            "15 - 5" to 10.0,
            "6 * 7" to 42.0,
            "10 / 4" to 2.5,
            "10 % 3" to 1.0,
            "2 ^ 10" to 1024.0,
            "2E7" to 2.0E7,
            "2 ^ 3 ^ 2" to 512.0,
            "10 - 4 - 3" to 3.0,
            "16 / 4 / 2" to 2.0,
            "6/2*(1+2)" to 9.0,
            "6/2*1+2" to 5.0,
            "1-(2)*3" to -5.0,

            // Comparison operators
            "3 > 2" to 1.0,
            "3 < 2" to 0.0,
            "3 = 3" to 1.0,
            "3 == 3" to 1.0,
            "3 != 4" to 1.0,
            "3 >= 3" to 1.0,
            "3 <= 2" to 0.0,

            // Boolean operators
            "true & true" to 1.0,
            "true && false" to 0.0,
            "true | false" to 1.0,
            "false || false" to 0.0,
            "true & (true & false | false)" to 0.0,

            // Unary operators
            "-1" to -1.0,
            "--1" to 1.0,
            "!true" to 0.0,
            "!false" to 1.0,
            "sin(0)" to 0.0,
            "cos(0)" to 1.0,
            "tan(0)" to 0.0,
            "sinh(0)" to 0.0,
            "cosh(0)" to 1.0,
            "tanh(0)" to 0.0,
            "asin(0)" to 0.0,
            "acos(1)" to 0.0,
            "atan(0)" to 0.0,
            "abs(-7)" to 7.0,
            "round(2.5)" to 3.0,
            "floor(2.9)" to 2.0,
            "ceil(2.1)" to 3.0,
            "log(1)" to 0.0,
            "sqrt(4)" to 2.0,
            "cbrt(8)" to 2.0,

            // Built-in functions that existed at baseline
            "min(3, 7)" to 3.0,
            "max(3, 7)" to 7.0,

            // Built-ins added by this upgrade
            "clamp(5, 0, 10)" to 5.0,
            "lerp(0, 10, 0.5)" to 5.0,
            "smoothstep(0, 1, 0.5)" to 0.5,
            "remap(0.5, 0, 1, 0, 100)" to 50.0,
            "exp(0)" to 1.0,
            "log10(100)" to 2.0,
            "log2(8)" to 3.0,
            "sign(-7)" to -1.0,
            "trunc(-2.9)" to -2.0,
            "hypot(3, 4)" to 5.0,
            "pow(2, 3)" to 8.0,
            "if(true, 1, 2)" to 1.0,
            "step(5, 5)" to 1.0,
            "min(5, 1, 9)" to 1.0,
            "max(5, 1, 9)" to 9.0
        )

        private val VARIABLE_CORPUS: List<Pair<String, Double>> = listOf(
            "x" to 3.0,
            "y" to 4.0,
            "x * y" to 12.0,
            "x + y * 2" to 11.0,
            "sqrt(x * x + y * y)" to 5.0,
            "min(x, y)" to 3.0,
            "max(x, y)" to 4.0,
            "x > y" to 0.0,
            "abs(x - y)" to 1.0
        )
    }
}
