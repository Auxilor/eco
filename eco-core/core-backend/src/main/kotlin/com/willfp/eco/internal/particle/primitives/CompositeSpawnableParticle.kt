package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleVars
import com.willfp.eco.internal.particle.ScopedSpawn
import com.willfp.eco.internal.particle.spawnWith
import org.bukkit.Location
import org.bukkit.event.Cancellable

/**
 * Type `composite` — calls every child at the same location.
 * Used to combine multiple effects into one.
 */
internal class CompositeSpawnableParticle(
    private val configuredAudience: ParticleAudience,
    private val vars: ParticleVars,
    private val children: List<SpawnableParticle>
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

        val compositeScope = vars.applyTo(outerScope)
        for (child in children) {
            child.spawnWith(location, context, effective, compositeScope)
        }
        return NoOpCancellable
    }
}