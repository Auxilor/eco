@file:JvmName("NumberUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.placeholder.context.PlaceholderContext

/**
 * Convert this number to a Roman numeral.
 *
 * The receiver is first truncated to an `Int`. Values from 1 to 4096 inclusive are converted
 * to a Roman numeral; anything outside that range is returned as its plain decimal string.
 *
 * @return The Roman numeral, or the plain decimal string if out of range.
 * @see NumberUtils.toNumeral
 */
fun Number.toNumeral(): String =
    NumberUtils.toNumeral(this.toInt())

/**
 * Format this number as a string with comma thousands separators.
 *
 * The receiver is first widened to a `Double`. The value is rounded to two decimal places,
 * and a trailing `.00` is stripped, so `1234.5` becomes `1,234.50` and `1234.0` becomes `1,234`.
 *
 * @return The formatted number.
 * @see NumberUtils.formatWithCommas
 */
fun Number.formatWithCommas(): String =
    NumberUtils.formatWithCommas(this.toDouble())

/**
 * Parse this string as a Roman numeral.
 *
 * @return The value of the numeral, or zero if the string is empty or does not start with a
 * recognised numeral.
 * @see NumberUtils.fromNumeral
 */
fun String.parseNumeral(): Int =
    NumberUtils.fromNumeral(this)

/**
 * Generate a random integer in a range, where both bounds are inclusive.
 *
 * @param min The minimum value, inclusive.
 * @param max The maximum value, inclusive.
 * @return The random integer, or min if min and max are equal.
 * @see NumberUtils.randInt
 */
fun randInt(min: Int, max: Int) =
    NumberUtils.randInt(min, max)

/**
 * Generate a random double in a range, where the minimum is inclusive and the maximum exclusive.
 *
 * The bounds are ordered internally, so passing them the wrong way round is safe. If they differ
 * by less than 1e-6 then min is returned.
 *
 * @param min The minimum value, inclusive.
 * @param max The maximum value, exclusive.
 * @return The random double.
 * @see NumberUtils.randFloat
 */
fun randDouble(min: Double, max: Double) =
    NumberUtils.randFloat(min, max)

/**
 * Generate a random float in a range, where the minimum is inclusive and the maximum exclusive.
 *
 * The value is generated as a double and narrowed to a float. The bounds are ordered internally,
 * so passing them the wrong way round is safe. If they differ by less than 1e-6 then min is returned.
 *
 * @param min The minimum value, inclusive.
 * @param max The maximum value, exclusive.
 * @return The random float.
 * @see NumberUtils.randFloat
 */
fun randFloat(min: Float, max: Float) =
    NumberUtils.randFloat(min.toDouble(), max.toDouble()).toFloat()

/**
 * Evaluate a mathematical expression with no placeholder context.
 *
 * @param expression The expression.
 * @return The value of the expression, or zero if it is invalid.
 * @see NumberUtils.evaluateExpression
 */
fun evaluateExpression(expression: String) =
    NumberUtils.evaluateExpression(expression)

/**
 * Evaluate a mathematical expression, translating placeholders against a context first.
 *
 * @param expression The expression.
 * @param context    The context to translate placeholders with respect to.
 * @return The value of the expression, or zero if it is invalid.
 * @see NumberUtils.evaluateExpression
 */
fun evaluateExpression(expression: String, context: PlaceholderContext) =
    NumberUtils.evaluateExpression(expression, context)

/**
 * Evaluate a mathematical expression, translating placeholders against a context first,
 * distinguishing an invalid expression from one that evaluates to zero.
 *
 * @param expression The expression.
 * @param context    The context to translate placeholders with respect to.
 * @return The value of the expression, or null if it is invalid.
 * @see NumberUtils.evaluateExpressionOrNull
 */
fun evaluateExpressionOrNull(expression: String, context: PlaceholderContext) =
    NumberUtils.evaluateExpressionOrNull(expression, context)
