package com.willfp.eco.internal.spigot.particle

import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleExpressionCompiler
import com.willfp.eco.internal.spigot.math.ExpressionParser
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv

internal fun installParticleExpressionCompiler() {
    ParticleExpressionCompiler.setImpl(::compileParticleExpression)
}

private fun compileParticleExpression(raw: String, varNames: List<String>): ParticleExpression {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) throw IllegalArgumentException("Empty expression")

    val isWrapped = trimmed.startsWith("{{") && trimmed.endsWith("}}")
    if (!isWrapped) {
        val num = trimmed.toDoubleOrNull()
        if (num != null) return ParticleExpression.Constant(num)
        if (looksLikeUnwrappedExpression(trimmed)) {
            throw IllegalArgumentException(
                "String '$raw' looks like an expression but is not wrapped. Did you mean \"{{ $trimmed }}\"?"
            )
        }
        throw IllegalArgumentException("Cannot parse '$raw' as a number or expression")
    }

    val inner = trimmed.substring(2, trimmed.length - 2).trim()
    if (inner.isEmpty()) throw IllegalArgumentException("Empty expression body in '$raw'")

    val hasVars = varNames.isNotEmpty() && containsAnyIdentifier(inner, varNames)

    return if (!hasVars) {
        val compiled = runCatching { ExpressionParser(inner, ExpressionEnv()).parse() }
            .getOrElse { throw IllegalArgumentException("Failed to compile '$raw': ${it.message}", it) }
        ParticleExpression.Constant(compiled.evaluate())
    } else {
        val env = ExpressionEnv().apply { setVariableNames(*varNames.toTypedArray()) }
        val compiled = runCatching { ExpressionParser(inner, env).parse() }
            .getOrElse { throw IllegalArgumentException("Failed to compile '$raw': ${it.message}", it) }
        ParticleExpression.Variable({ values -> compiled.evaluate(*values) }, varNames)
    }
}

private fun containsAnyIdentifier(inner: String, names: List<String>): Boolean {
    for (name in names) {
        if (Regex("\\b${Regex.escape(name)}\\b").containsMatchIn(inner)) return true
    }
    return false
}

private fun looksLikeUnwrappedExpression(s: String): Boolean {
    if (s.contains('+') || s.contains('*') || s.contains('/') || s.contains('^')) return true
    if (s.contains(Regex("[A-Za-z_][A-Za-z0-9_]*\\("))) return true
    return false
}
