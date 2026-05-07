package com.willfp.eco.internal.particle

/**
 * A pre-compiled `vars:` block. Holds an ordered list of (name, expression)
 * pairs. At evaluation, each expression is computed against the current
 * scope and the result is layered into the scope so later vars in the
 * same block can reference earlier ones.
 */
internal class ParticleVars(
    private val entries: List<Entry>
) {
    data class Entry(val name: String, val expression: ParticleExpression, val varOrder: List<String>)

    fun applyTo(scope: EvaluationScope): EvaluationScope {
        if (entries.isEmpty()) return scope
        val accumulated = mutableMapOf<String, Double>()
        var current = scope
        for (entry in entries) {
            val values = DoubleArray(entry.varOrder.size) { i ->
                current.lookup(entry.varOrder[i])
            }
            val result = entry.expression.evaluate(values)
            accumulated[entry.name] = result
            current = scope.withVars(accumulated.toMap())
        }
        return current
    }

    companion object {
        val EMPTY = ParticleVars(emptyList())
    }
}