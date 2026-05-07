package com.willfp.eco.internal.particle

import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import org.bukkit.Location
import org.bukkit.event.Cancellable

/**
 * Internal extension of [SpawnableParticle] that accepts a parent [EvaluationScope].
 * Lets tick/iterate/composite primitives pass their computed vars down to children,
 * so inner presets can reference outer vars like `angle` or `pick`.
 *
 * Public [SpawnableParticle] implementors (other plugins) don't implement this;
 * [spawnWith] falls back to the normal [SpawnableParticle.spawn] for them.
 */
internal interface ScopedSpawn {
    fun spawnScoped(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience,
        outerScope: EvaluationScope
    ): Cancellable
}

internal fun SpawnableParticle.spawnWith(
    location: Location,
    context: PlaceholderContext,
    audience: ParticleAudience,
    scope: EvaluationScope
): Cancellable = if (this is ScopedSpawn) {
    spawnScoped(location, context, audience, scope)
} else {
    spawn(location, context, audience)
}