package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import org.bukkit.entity.Player
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.data.FastNumberParsing
import redempt.crunch.functional.EvaluationEnvironment

private val cache = mutableMapOf<String, CompiledExpression>()
private val goToZero = Crunch.compileExpression("0")

fun evaluateExpression(expression: String, player: Player?): Double {
    val placeholderValues = PlaceholderManager.findPlaceholdersIn(expression)
        .map { PlaceholderManager.getResult(player, expression) }
        .map { runCatching { FastNumberParsing.parseDouble(it) }.getOrDefault(0.0) }
        .toDoubleArray()

    val compiled = generateExpression(expression)
    return runCatching { compiled.evaluate(*placeholderValues) }.getOrDefault(0.0)
}

private fun generateExpression(expression: String): CompiledExpression {
    val cached = cache[expression]

    if (cached != null) {
        return cached
    }

    val placeholders = PlaceholderManager.findPlaceholdersIn(expression)

    val env = EvaluationEnvironment()
    env.setVariableNames(*placeholders.toTypedArray())

    val compiled = runCatching { Crunch.compileExpression(expression, env) }.getOrDefault(goToZero)
    cache[expression] = compiled
    return compiled
}
