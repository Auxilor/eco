package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.sanitiseCount
import com.willfp.eco.internal.particle.sanitiseDouble
import org.bukkit.Location
import org.bukkit.event.Cancellable

/**
 * Type `iterate` — calls [child] [countExpr] times, with reserved variables
 * `i` and `n` set on each call. Each invocation can displace the location
 * by [offsetXExpr] / [offsetYExpr] / [offsetZExpr] (evaluated per-point).
 */
internal class IterateSpawnableParticle(
    private val countExpr: ParticleExpression,
    private val offsetXExpr: ParticleExpression,
    private val offsetYExpr: ParticleExpression,
    private val offsetZExpr: ParticleExpression,
    private val configuredAudience: ParticleAudience,
    private val vars: ParticleVars,
    private val countVarNames: List<String>,
    private val pointVarNames: List<String>,
    private val child: SpawnableParticle
) : SpawnableParticle {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable {
        val effective =
            if (audience === ParticleAudience.DEFAULT) configuredAudience else audience

        val baseScope = vars.applyTo(EvaluationScope.empty(context))
        val countValues = DoubleArray(countVarNames.size) { i -> baseScope.lookup(countVarNames[i]) }
        val n = sanitiseCount(countExpr.evaluate(countValues))
        if (n <= 0) return NoOpCancellable

        for (i in 0 until n) {
            val pointScope = baseScope.withReserved(mapOf("i" to i.toDouble(), "n" to n.toDouble()))
            val values = DoubleArray(pointVarNames.size) { j -> pointScope.lookup(pointVarNames[j]) }
            val ox = sanitiseDouble(offsetXExpr.evaluate(values))
            val oy = sanitiseDouble(offsetYExpr.evaluate(values))
            val oz = sanitiseDouble(offsetZExpr.evaluate(values))
            child.spawn(location.clone().add(ox, oy, oz), context, effective)
        }
        return NoOpCancellable
    }
}