package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import org.bukkit.entity.Player
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.data.FastNumberParsing
import redempt.crunch.functional.EvaluationEnvironment

private val cache: Cache<String, CompiledExpression> = Caffeine.newBuilder().build()
private val goToZero = Crunch.compileExpression("0")

fun evaluateExpression(expression: String, player: Player?, context: PlaceholderInjectable): Double {
    val placeholderValues = PlaceholderManager.findPlaceholdersIn(expression)
        .map { PlaceholderManager.translatePlaceholders(it, player, context) }
        .map { runCatching { FastNumberParsing.parseDouble(it) }.getOrDefault(0.0) }
        .toDoubleArray()

    val compiled = cache.get(expression) {
        val placeholders = PlaceholderManager.findPlaceholdersIn(it)
        val env = EvaluationEnvironment()
        env.setVariableNames(*placeholders.toTypedArray())
        runCatching { Crunch.compileExpression(expression, env) }.getOrDefault(goToZero)
    }

    return runCatching { compiled.evaluate(*placeholderValues) }.getOrDefault(0.0)
}
