package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.StaticPlaceholder
import org.bukkit.entity.Player
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.data.FastNumberParsing
import redempt.crunch.functional.EvaluationEnvironment

private val cache: Cache<String, CompiledExpression> = Caffeine.newBuilder().build()
private val goToZero = Crunch.compileExpression("0")

fun evaluateExpression(expression: String, player: Player?, statics: Iterable<StaticPlaceholder>): Double {
    val placeholderValues = PlaceholderManager.findPlaceholdersIn(expression)
        .map { PlaceholderManager.translatePlaceholders(it, player) }
        .union(statics.map { it.value })
        .toList()
        .map { runCatching { FastNumberParsing.parseDouble(it) }.getOrDefault(0.0) }
        .toDoubleArray()

    val compiled = cache.get(expression) {
        val placeholders = PlaceholderManager.findPlaceholdersIn(it) union statics.map { static -> "%${static.identifier}%" }
        val env = EvaluationEnvironment()
        env.setVariableNames(*placeholders.toTypedArray())
        runCatching { Crunch.compileExpression(expression, env) }.getOrDefault(goToZero)
    }

    return runCatching { compiled.evaluate(*placeholderValues) }.getOrDefault(0.0)
}
