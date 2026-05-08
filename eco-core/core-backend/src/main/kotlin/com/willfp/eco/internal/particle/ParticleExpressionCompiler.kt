package com.willfp.eco.internal.particle

/**
 * Compiles raw YAML field strings into [ParticleExpression]s with constant
 * folding when possible. The actual math compilation is provided by core-plugin
 * via [setImpl] before any particles are loaded.
 *
 * Conventions:
 *  - A bare numeric literal compiles to [ParticleExpression.Constant].
 *  - A `"{{ ... }}"`-wrapped string is compiled via the math expression system.
 *  - If the wrapped expression has no declared variable references, it is
 *    evaluated once at load time and stored as a constant.
 *  - If the string contains math-y characters but no `{{ }}` wrapper, the
 *    compiler throws [IllegalArgumentException] with a hint.
 */
object ParticleExpressionCompiler {

    @Volatile
    private var impl: ((String, List<String>) -> ParticleExpression)? = null

    fun setImpl(compiler: (String, List<String>) -> ParticleExpression) {
        impl = compiler
    }

    fun compile(raw: String, varNames: List<String>): ParticleExpression =
        (impl ?: error("ParticleExpressionCompiler not initialised"))
            .invoke(raw, varNames)
}
