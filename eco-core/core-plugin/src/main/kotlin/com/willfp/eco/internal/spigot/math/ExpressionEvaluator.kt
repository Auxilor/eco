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
    if (none { it in '0'..'9' }) return null

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

    private val resultCache: EcoCache<String, Double> = EcoCache.builder<String, Double>()
        .expireAfterWrite(Duration.ofMillis(resultCacheTtlMs))
        .build()

    private val threadLocalArrays = ThreadLocal.withInitial { HashMap<Int, DoubleArray>() }

    private val nestedCompilationCache: EcoCache<String, Any> = EcoCache.builder<String, Any>()
        .maxSize(MAX_NESTED_CACHE_SIZE)
        .build()

    private val nestedState = ThreadLocal.withInitial { NestedState() }

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

        val prepared = prepare(expression) ?: return null

        // Two bypasses, for different reasons:
        //  - Non-deterministic: caching freezes a single random draw for the whole TTL window.
        //  - Placeholder-bearing: resolved values are not in the key, so a cached result goes
        //    stale the moment a placeholder changes. Putting them in the key would require
        //    resolving first, which is the very cost the cache existed to avoid.
        if (!prepared.isDeterministic || prepared.placeholderCount > 0) {
            return validate(evaluateFresh(prepared, context))
        }

        resultCache[expression]?.let { hits++; return it }

        val validated = validate(evaluateFresh(prepared, context))
        if (validated != null) {
            writes++
            resultCache.put(expression, validated)
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
            var startsBudget = true
            for (v in values) {
                arr[i++] = v.fastToDoubleOrNull() ?: evaluateNested(v, context, startsBudget)
                    .also { startsBudget = false }
            }
            runCatching { prepared.compiled.evaluate(*arr) }.getOrNull()
        }

    private fun evaluateNested(value: String, context: PlaceholderContext, startsBudget: Boolean): Double {
        val state = nestedState.get()
        if (startsBudget && state.depth == 0) {
            state.remaining = MAX_NESTED_EVALUATIONS
        }

        val canExpand = state.depth < MAX_NESTING_DEPTH && state.remaining > 0
        val prepared = if (canExpand && value.any { it in NESTED_VALUE_MARKERS }) {
            nestedCompilationCache.get(value) { compile(it) ?: CompilationFailed }
        } else {
            CompilationFailed
        }

        if (prepared !is PreparedExpression) {
            return 0.0
        }

        state.remaining--
        state.depth++
        return try {
            validate(evaluateFresh(prepared, context)) ?: 0.0
        } finally {
            state.depth--
        }
    }

    private fun prepare(expression: String): PreparedExpression? =
        compilationCache.getOrPut(expression) { compile(expression) ?: CompilationFailed } as? PreparedExpression

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

    private fun getArray(size: Int): DoubleArray {
        if (nestedState.get().depth > 0) {
            return DoubleArray(size)
        }
        return threadLocalArrays.get().getOrPut(size) { DoubleArray(size) }
    }

    private class NestedState {
        var depth = 0
        var remaining = 0
    }

    private class PreparedExpression(
        val compiled: CompiledExpression,
        val placeholderNames: List<String>
    ) {
        val placeholderCount = placeholderNames.size
        val isDeterministic = compiled.isDeterministic()
    }

    companion object {
        private const val MAX_NESTING_DEPTH = 8

        private const val MAX_NESTED_EVALUATIONS = 64

        private const val MAX_NESTED_CACHE_SIZE = 1024L

        private const val NESTED_VALUE_MARKERS = "0123456789%+-*/^()<>=!&|"

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
