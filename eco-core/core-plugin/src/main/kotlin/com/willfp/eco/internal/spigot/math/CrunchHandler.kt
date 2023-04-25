package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.parsing.PlaceholderContext
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.data.FastNumberParsing
import redempt.crunch.functional.EvaluationEnvironment
import redempt.crunch.functional.Function
import kotlin.math.max
import kotlin.math.min

private val cache: Cache<String, CompiledExpression> = Caffeine.newBuilder().build()
private val goToZero = Crunch.compileExpression("0")

private val min = Function("min", 2) {
    min(it[0], it[1])
}

private val max = Function("max", 2) {
    max(it[0], it[1])
}

fun evaluateExpression(expression: String, context: PlaceholderContext) =
    doEvaluateExpression(
        expression,
        context
    ).let { if (!it.isFinite()) 0.0 else it } // Fixes NaN bug.

private fun doEvaluateExpression(
    expression: String,
    context: PlaceholderContext
): Double {
    val placeholderValues = PlaceholderManager.findPlaceholdersIn(expression)
        .map { PlaceholderManager.translatePlaceholders(it, context) }
        .map { runCatching { FastNumberParsing.parseDouble(it) }.getOrDefault(0.0) }
        .toDoubleArray()

    val compiled = cache.get(expression) {
        val placeholders = PlaceholderManager.findPlaceholdersIn(it)
        val env = EvaluationEnvironment()
        env.setVariableNames(*placeholders.toTypedArray())
        env.addFunctions(min, max)
        runCatching { Crunch.compileExpression(expression, env) }.getOrDefault(goToZero)
    }

    return runCatching { compiled.evaluate(*placeholderValues) }.getOrDefault(0.0)
}
