package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleExpression
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.ScopedSpawn
import com.willfp.eco.internal.particle.sanitiseCount
import com.willfp.eco.internal.particle.sanitiseDouble
import com.willfp.eco.internal.particle.spawnWith
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
) : SpawnableParticle, ScopedSpawn {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable = spawnScoped(location, context, audience, EvaluationScope.empty(context))

    override fun spawnScoped(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience,
        outerScope: EvaluationScope
    ): Cancellable {
        val effective =
            if (audience === ParticleAudience.DEFAULT) configuredAudience else audience

        val countValues = DoubleArray(countVarNames.size) { j -> outerScope.lookup(countVarNames[j]) }
        val n = sanitiseCount(countExpr.evaluate(countValues))
        if (n <= 0) return NoOpCancellable

        for (i in 0 until n) {
            val reservedScope = outerScope.withReserved(mapOf("i" to i.toDouble(), "n" to n.toDouble()))
            val pointScope = vars.applyTo(reservedScope)
            val values = DoubleArray(pointVarNames.size) { j -> pointScope.lookup(pointVarNames[j]) }
            val ox = sanitiseDouble(offsetXExpr.evaluate(values))
            val oy = sanitiseDouble(offsetYExpr.evaluate(values))
            val oz = sanitiseDouble(offsetZExpr.evaluate(values))
            child.spawnWith(location.clone().add(ox, oy, oz), context, effective, pointScope)
        }
        return NoOpCancellable
    }
}