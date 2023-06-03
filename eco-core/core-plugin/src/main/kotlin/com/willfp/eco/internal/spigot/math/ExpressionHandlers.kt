package com.willfp.eco.internal.spigot.math

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.placeholder.PlaceholderParser
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.functional.EvaluationEnvironment
import redempt.crunch.functional.Function
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private val min = Function("min", 2) {
    min(it[0], it[1])
}

private val max = Function("max", 2) {
    max(it[0], it[1])
}

interface ExpressionHandler {
    fun evaluate(expression: String, context: PlaceholderContext): Double?
}

private fun String.fastToDoubleOrNull(): Double? {
    if (isEmpty()) {
        return null
    }

    var idx = 0
    val isNegative = this[0] == '-'
    if (isNegative) idx++

    var integerPart = 0.0
    var decimalPart = 0.0
    var decimalIdx = -1

    while (idx < length) {

        when (val char = this[idx]) {
            '.' -> {
                if (decimalIdx != -1) return null
                decimalIdx = idx
            }

            in '0'..'9' -> {
                val number = (char.code - '0'.code).toDouble()
                if (decimalIdx != -1) {
                    decimalPart = decimalPart * 10 + number
                } else {
                    integerPart = integerPart * 10 + number
                }
            }

            else -> return null
        }

        idx++
    }

    decimalPart /= 10.0.pow((length - decimalIdx - 1).toDouble())

    return if (isNegative) -(integerPart + decimalPart) else integerPart + decimalPart
}


class ImmediatePlaceholderTranslationExpressionHandler(
    private val placeholderParser: PlaceholderParser
) : ExpressionHandler {
    private val cache: Cache<String, CompiledExpression?> = Caffeine.newBuilder()
        .expireAfterAccess(500, TimeUnit.MILLISECONDS)
        .build()

    private val env = EvaluationEnvironment().apply {
        addFunctions(min, max)
    }

    override fun evaluate(expression: String, context: PlaceholderContext): Double? {
        val translatedExpression = placeholderParser.translatePlacholders(expression, context)

        val compiled = cache.get(translatedExpression) {
            runCatching { Crunch.compileExpression(translatedExpression, env) }.getOrNull()
        }

        return runCatching { compiled?.evaluate() }.getOrNull()
    }
}

class LazyPlaceholderTranslationExpressionHandler(
    private val placeholderParser: PlaceholderParser
) : ExpressionHandler {
    private val cache: Cache<String, CompiledExpression?> = Caffeine.newBuilder()
        .build()

    override fun evaluate(expression: String, context: PlaceholderContext): Double? {
        val placeholders = PlaceholderManager.findPlaceholdersIn(expression)

        val placeholderValues = placeholderParser.parseIndividualPlaceholders(placeholders, context)
            .map { it.fastToDoubleOrNull() ?: 0.0 }
            .toDoubleArray()

        val compiled = cache.get(expression) {
            val env = EvaluationEnvironment()
            env.setVariableNames(*placeholders.toTypedArray())
            env.addFunctions(min, max)
            runCatching { Crunch.compileExpression(expression, env) }.getOrNull()
        }

        return runCatching { compiled?.evaluate(*placeholderValues) }.getOrNull()
    }
}
