package com.willfp.eco.internal.particle

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import kotlin.math.E
import kotlin.math.PI

/**
 * The variable-resolution chain consulted at expression evaluation time.
 *
 * Lookup order (per spec §8.3):
 *  1. Reserved variables (`t`, `i`, `n` — set by `tick`/`iterate` primitives).
 *  2. Vars cascade (innermost wins on collision).
 *  3. Math constants (`pi`, `e`).
 *  4. Placeholders from [PlaceholderContext], resolved as `%name%` → numeric.
 *
 * Unknown names resolve to `0.0` (never throw at evaluation time).
 *
 * Scopes are immutable; [withReserved] / [withVars] create a derived child.
 */
internal class EvaluationScope private constructor(
    val context: PlaceholderContext,
    private val reservedStack: List<Map<String, Double>>,
    private val varsStack: List<Map<String, Double>>
) {

    fun withReserved(values: Map<String, Double>): EvaluationScope =
        EvaluationScope(context, reservedStack + values, varsStack)

    fun withVars(values: Map<String, Double>): EvaluationScope =
        EvaluationScope(context, reservedStack, varsStack + values)

    fun lookup(name: String): Double {
        // 1. Reserved (innermost wins)
        for (i in reservedStack.indices.reversed()) {
            val v = reservedStack[i][name]
            if (v != null) return v
        }
        // 2. Vars (innermost wins)
        for (i in varsStack.indices.reversed()) {
            val v = varsStack[i][name]
            if (v != null) return v
        }
        // 3. Constants
        when (name) {
            "pi" -> return PI
            "e" -> return E
        }
        // 4. Placeholder lookup
        val raw = resolvePlaceholder("%$name%") ?: resolvePlaceholder(name)
        return raw?.toDoubleOrNull() ?: 0.0
    }

    private fun resolvePlaceholder(token: String): String? {
        return try {
            val result = PlaceholderManager.translatePlaceholders(token, context)
            result.takeUnless { it == token }
        } catch (_: Throwable) {
            null
        }
    }

    companion object {
        fun empty(context: PlaceholderContext): EvaluationScope =
            EvaluationScope(context, emptyList(), emptyList())
    }
}