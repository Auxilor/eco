package com.willfp.eco.internal.particle.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.particle.ParticleAudience
import com.willfp.eco.core.particle.SpawnableParticle
import com.willfp.eco.core.particle.impl.EmptyParticle
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.internal.particle.ParticleScope
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.event.Cancellable

/**
 * A preset stored in the registry. When invoked directly (no overrides), it
 * instantiates with parameter defaults. Consumers normally reference a preset
 * via the YAML loader, which calls [instantiate] with their override map.
 */
internal class PresetSpawnableParticle(
    val key: NamespacedKey,
    val parameters: Map<String, Any>,
    val bodyTemplate: Map<String, Any>,
    private val plugin: EcoPlugin,
    private val loader: () -> ParticleConfigLoader
) : SpawnableParticle {

    override fun spawn(
        location: Location,
        context: PlaceholderContext,
        audience: ParticleAudience
    ): Cancellable {
        val instantiated = instantiate(emptyMap(), ParticleScope(resolvingChain = listOf(key)))
        return instantiated.spawn(location, context, audience)
    }

    /**
     * Instantiate the preset with optional overrides.
     *
     * @param overrides Caller-supplied parameter overrides.
     * @param scope     Loader scope (carries resolution chain for cycle detection).
     * @return A fully-instantiated [SpawnableParticle].
     */
    fun instantiate(overrides: Map<String, Any>, scope: ParticleScope): SpawnableParticle {
        val effectiveParams = parameters + overrides
        val effectiveScope = scope.withParameters(effectiveParams)
        return try {
            loader().loadEntry(bodyTemplate, plugin, effectiveScope)
        } catch (ex: Throwable) {
            plugin.logger.warning("Failed to instantiate preset $key: ${ex.message}")
            EmptyParticle.INSTANCE
        }
    }
}