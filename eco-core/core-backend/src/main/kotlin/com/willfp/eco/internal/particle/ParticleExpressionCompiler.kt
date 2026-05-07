package com.willfp.eco.internal.particle

import redempt.crunch.Crunch
import redempt.crunch.functional.ExpressionEnv
import redempt.crunch.functional.Function
import java.util.OptionalInt
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.E
import kotlin.math.PI

/**
 * Compiles raw YAML field strings into [ParticleExpression]s with constant
 * folding when possible.
 *
 * Conventions:
 *  - A bare numeric literal compiles to [ParticleExpression.Constant].
 *  - A `"{{ ... }}"`-wrapped string is compiled via Crunch.
 *  - If the wrapped expression has no declared variable references, it is
 *    evaluated once at load time and stored as a constant.
 *  - If the string contains math-y characters but no `{{ }}` wrapper, the
 *    compiler throws [IllegalArgumentException] with a hint.
 */
internal object ParticleExpressionCompiler {

    private val MIN = Function("min", OptionalInt.of(2)) { args -> minOf(args[0], args[1]) }
    private val MAX = Function("max", OptionalInt.of(2)) { args -> maxOf(args[0], args[1]) }
    private val RANDOM = Function("random", OptionalInt.of(2)) { args ->
        val a = args[0]
        val b = args[1]
        val lo = minOf(a, b)
        val hi = maxOf(a, b)
        if (lo == hi) lo else ThreadLocalRandom.current().nextDouble(lo, hi)
    }

    /**
     * Compile a field value.
     *
     * @param raw      Raw text (post `$param` substitution by the caller, if any).
     * @param varNames Names of variables this expression may reference, in order
     *                 matching the [DoubleArray] passed to [ParticleExpression.evaluate].
     * @throws IllegalArgumentException if the string is malformed.
     */
    fun compile(raw: String, varNames: List<String>): ParticleExpression {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            throw IllegalArgumentException("Empty expression")
        }

        val isWrapped = trimmed.startsWith("{{") && trimmed.endsWith("}}")
        if (!isWrapped) {
            val num = trimmed.toDoubleOrNull()
            if (num != null) {
                return ParticleExpression.Constant(num)
            }
            if (looksLikeUnwrappedExpression(trimmed)) {
                throw IllegalArgumentException(
                    "String '$raw' looks like an expression but is not wrapped. " +
                        "Did you mean \"{{ $trimmed }}\"?"
                )
            }
            throw IllegalArgumentException("Cannot parse '$raw' as a number or expression")
        }

        val inner = trimmed.substring(2, trimmed.length - 2).trim()
        if (inner.isEmpty()) {
            throw IllegalArgumentException("Empty expression body in '$raw'")
        }

        val env = newEnv()

        val hasVars = varNames.isNotEmpty() && containsAnyIdentifier(inner, varNames)
        return if (!hasVars) {
            try {
                val compiled = Crunch.compileExpression(inner, env)
                ParticleExpression.Constant(compiled.evaluate())
            } catch (ex: Throwable) {
                throw IllegalArgumentException("Failed to compile '$raw': ${ex.message}", ex)
            }
        } else {
            try {
                env.setVariableNames(*varNames.toTypedArray())
                val compiled = Crunch.compileExpression(inner, env)
                ParticleExpression.Variable(compiled, varNames)
            } catch (ex: Throwable) {
                throw IllegalArgumentException("Failed to compile '$raw': ${ex.message}", ex)
            }
        }
    }

    private fun newEnv(): ExpressionEnv {
        val env = ExpressionEnv()
        env.addFunction(MIN)
        env.addFunction(MAX)
        env.addFunction(RANDOM)
        return env
    }

    private fun containsAnyIdentifier(inner: String, names: List<String>): Boolean {
        if (names.isEmpty()) return false
        for (name in names) {
            if (Regex("\\b" + Regex.escape(name) + "\\b").containsMatchIn(inner)) {
                return true
            }
        }
        return false
    }

    private fun looksLikeUnwrappedExpression(s: String): Boolean {
        if (s.contains('%')) return true
        if (s.contains(Regex("[+\\-*/^]"))) return true
        if (s.contains(Regex("[A-Za-z_][A-Za-z0-9_]*\\("))) return true
        return false
    }

    @Suppress("unused") internal val PI_VALUE = PI
    @Suppress("unused") internal val E_VALUE = E
}