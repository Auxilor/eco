package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.data.FastNumberParsing
import redempt.crunch.functional.EvaluationEnvironment
import redempt.crunch.functional.Function
import java.util.Objects
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

private val cache: Cache<String, CompiledExpression> = Caffeine.newBuilder().build()

private val evaluationCache: Cache<Int, Double> = Caffeine.newBuilder()
    .expireAfterWrite(100, TimeUnit.MILLISECONDS)
    .build()

private val goToZero = Crunch.compileExpression("0")

private val min = Function("min", 2) {
    min(it[0], it[1])
}

private val max = Function("max", 2) {
    max(it[0], it[1])
}

fun evaluateExpression(expression: String, context: PlaceholderContext): Double {
    val hash = Objects.hash(
        expression,
        context.player?.uniqueId,
        context.injectableContext
    )

    return evaluationCache.get(hash) {
        doEvaluateExpression(
            expression,
            context
        ).let { if (!it.isFinite()) 0.0 else it } // Fixes NaN bug.
    }
}

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
