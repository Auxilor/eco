package com.willfp.eco.internal.particle

/**
 * A pre-compiled expression for a particle field. Sealed:
 * [Constant] is the load-time-folded form (no variables, no placeholders);
 * [Variable] is the per-spawn-evaluated form bound to a list of variable names.
 */
sealed class ParticleExpression {

    /** Evaluate against the given variable values (positions match the names list given at compile time). */
    abstract fun evaluate(values: DoubleArray): Double

    /** Constant value computed once at load time. */
    class Constant(val value: Double) : ParticleExpression() {
        override fun evaluate(values: DoubleArray): Double = value
    }

    /**
     * Variable expression bound to [varNames]. The values passed to [evaluate]
     * must be in the same order as [varNames].
     */
    class Variable(
        private val compiled: (DoubleArray) -> Double,
        val varNames: List<String>
    ) : ParticleExpression() {
        override fun evaluate(values: DoubleArray): Double = compiled(values)
    }
}
