package com.willfp.eco.internal.particle.primitives

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.EvaluationScope
import com.willfp.eco.internal.particle.NoOpCancellable
import com.willfp.eco.internal.particle.ParticleVars
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
) : SpawnableParticle {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable {
        val effective =
            if (audience === ParticleAudience.DEFAULT) configuredAudience else audience

        @Suppress("UNUSED_VARIABLE")
        val scope = vars.applyTo(EvaluationScope.empty(context))

        for (child in children) {
            child.spawn(location, context, effective)
        }
        return NoOpCancellable
    }
}