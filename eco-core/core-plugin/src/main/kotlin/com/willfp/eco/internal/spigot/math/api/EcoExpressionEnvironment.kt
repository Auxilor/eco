package com.willfp.eco.internal.spigot.math.api

import com.willfp.eco.core.math.CompiledExpression
import com.willfp.eco.core.math.ExpressionEnvironment
import com.willfp.eco.internal.spigot.math.ExpressionParser
import com.willfp.eco.internal.spigot.math.functional.ExpressionEnv
import com.willfp.eco.core.math.ExpressionCompilationException as PublicCompilationException

/**
 * Adapts the internal [ExpressionEnv] to the public [ExpressionEnvironment] contract.
 *
 * The wrapped environment is never mutated after construction, so this is safe to share across
 * threads: [ExpressionParser] holds all per-compilation state.
 */
class EcoExpressionEnvironment(
    private val env: ExpressionEnv
) : ExpressionEnvironment {

    override fun compile(expression: String): CompiledExpression? =
        try {
            EcoCompiledExpression(ExpressionParser(expression, env).parse())
        } catch (e: Exception) {
            null
        }

    override fun compileOrThrow(expression: String): CompiledExpression {
        val parser = ExpressionParser(expression, env)
        try {
            return EcoCompiledExpression(parser.parse())
        } catch (e: Exception) {
            throw PublicCompilationException(
                e.message ?: "Failed to compile expression",
                expression,
                parser.cursor
            )
        }
    }
}
