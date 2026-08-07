package com.willfp.eco.internal.spigot.math

import com.willfp.eco.core.cache.EcoCache
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.placeholder.PlaceholderParser
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import java.util.concurrent.ConcurrentHashMap
import java.time.Duration
import kotlin.math.pow

fun String.fastToDoubleOrNull(): Double? {
    val len = length
    if (len == 0) return null

    var idx = 0
    val isNegative = this[0] == '-'
    if (isNegative) idx++

    var integerPart = 0.0
    var decimalPart = 0.0
    var decimalIdx = -1

    while (idx < len) {
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

    if (decimalIdx != -1) {
        decimalPart /= 10.0.pow((len - decimalIdx - 1).toDouble())
    }

    return if (isNegative) -(integerPart + decimalPart) else integerPart + decimalPart
}

class ExpressionEvaluator(
    private val placeholderParser: PlaceholderParser,
    resultCacheTtlMs: Long
) {
    private val compilationCache = ConcurrentHashMap<String, Any>()

    private val resultCache: EcoCache<Long, Double> = EcoCache.builder<Long, Double>()
        .expireAfterWrite(Duration.ofMillis(resultCacheTtlMs))
        .build()

    private val threadLocalArrays = ThreadLocal.withInitial { HashMap<Int, DoubleArray>() }

    @Volatile
    private var hits = 0L

    @Volatile
    private var writes = 0L

    /** Result cache hits. Instrumentation for tests; not part of any public contract. */
    internal val cacheHits: Long
        get() = hits

    /** Result cache writes. Instrumentation for tests; not part of any public contract. */
    internal val cacheWrites: Long
        get() = writes

    /** Zero the instrumentation counters. */
    internal fun resetInstrumentation() {
        hits = 0L
        writes = 0L
    }

    private object CompilationFailed

    fun evaluate(expression: String, context: PlaceholderContext): Double? {
        expression.fastToDoubleOrNull()?.let { return it }

        val cached = compilationCache.getOrPut(expression) { compile(expression) ?: CompilationFailed }
        val prepared = cached as? PreparedExpression ?: return null

        // Two bypasses, for different reasons:
        //  - Non-deterministic: caching freezes a single random draw for the whole TTL window.
        //  - Placeholder-bearing: resolved values are not in the key, so a cached result goes
        //    stale the moment a placeholder changes. Putting them in the key would require
        //    resolving first, which is the very cost the cache existed to avoid.
        if (!prepared.isDeterministic || prepared.placeholderCount > 0) {
            return validate(evaluateFresh(prepared, context))
        }

        val cacheKey = resultCacheKey(expression, context)
        resultCache.get(cacheKey)?.let { hits++; return it }

        val validated = validate(evaluateFresh(prepared, context))
        if (validated != null) {
            writes++
            resultCache.put(cacheKey, validated)
        }
        return validated
    }

    private fun evaluateFresh(prepared: PreparedExpression, context: PlaceholderContext): Double? =
        if (prepared.placeholderCount == 0) {
            runCatching { prepared.compiled.evaluate() }.getOrNull()
        } else {
            val values = placeholderParser.parseIndividualPlaceholders(
                prepared.placeholderNames, context
            )
            val arr = getArray(prepared.placeholderCount)
            var i = 0
            for (v in values) {
                arr[i++] = v.fastToDoubleOrNull() ?: 0.0
            }
            runCatching { prepared.compiled.evaluate(*arr) }.getOrNull()
        }

    private fun validate(result: Double?): Double? =
        if (result?.isFinite() == true) result else null

    private fun compile(expression: String): PreparedExpression? {
        val placeholders = findPlaceholders(expression)
        val env = ExpressionEnv().apply {
            if (placeholders.isNotEmpty()) {
                setVariableNames(*placeholders.toTypedArray())
            }
        }
        val compiled = runCatching {
            ExpressionParser(expression, env).parse()
        }.getOrNull() ?: return null
        return PreparedExpression(compiled, placeholders)
    }

    private fun resultCacheKey(expression: String, context: PlaceholderContext): Long {
        var hash = expression.hashCode().toLong()
        hash = hash * 31 + (context.player?.uniqueId?.hashCode()?.toLong() ?: 0L)
        hash = hash * 31 + context.injectableContext.hashCode().toLong()
        return hash
    }

    private fun getArray(size: Int): DoubleArray {
        return threadLocalArrays.get().getOrPut(size) { DoubleArray(size) }
    }

    private class PreparedExpression(
        val compiled: CompiledExpression,
        val placeholderNames: List<String>
    ) {
        val placeholderCount = placeholderNames.size
        val isDeterministic = compiled.isDeterministic()
    }

    companion object {
        fun findPlaceholders(expression: String): List<String> {
            val result = mutableListOf<String>()
            var i = 0
            while (i < expression.length) {
                if (expression[i] == '%') {
                    val start = i
                    i++
                    while (i < expression.length && expression[i] != '%' && expression[i] != ' ') {
                        i++
                    }
                    if (i < expression.length && expression[i] == '%' && i > start + 1) {
                        val placeholder = expression.substring(start, i + 1)
                        if (placeholder !in result) {
                            result.add(placeholder)
                        }
                    }
                }
                i++
            }
            return result
        }
    }
}
