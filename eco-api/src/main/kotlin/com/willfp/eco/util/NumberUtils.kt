@file:JvmName("NumberUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.placeholder.context.PlaceholderContext

/** @see NumberUtils.toNumeral */
fun Number.toNumeral(): String =
    NumberUtils.toNumeral(this.toInt())

/** @see NumberUtils.fromNumeral */
fun String.parseNumeral(): Int =
    NumberUtils.fromNumeral(this)

/** @see NumberUtils.randInt */
fun randInt(min: Int, max: Int) =
    NumberUtils.randInt(min, max)

/** @see NumberUtils.randFloat */
fun randDouble(min: Double, max: Double) =
    NumberUtils.randFloat(min, max)

/** @see NumberUtils.randFloat */
fun randFloat(min: Float, max: Float) =
    NumberUtils.randFloat(min.toDouble(), max.toDouble()).toFloat()

/** @see NumberUtils.evaluateExpression */
fun evaluateExpression(expression: String) =
    NumberUtils.evaluateExpression(expression)

/** @see NumberUtils.evaluateExpression */
fun evaluateExpression(expression: String, context: PlaceholderContext) =
    NumberUtils.evaluateExpression(expression, context)

/** @see NumberUtils.evaluateExpressionOrNull */
fun evaluateExpressionOrNull(expression: String, context: PlaceholderContext) =
    NumberUtils.evaluateExpressionOrNull(expression, context)
