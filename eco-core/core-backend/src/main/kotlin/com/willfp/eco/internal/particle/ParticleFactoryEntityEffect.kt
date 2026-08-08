package com.willfp.eco.internal.particle

import com.willfp.eco.core.particle.ParticleFactory
import com.willfp.eco.core.particle.SpawnableParticle
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

/**
 * Creates coloured entity effect particles, the swirls used by potion effects,
 * e.g. `entity_effect:15fe2f`.
 */
object ParticleFactoryEntityEffect : ParticleFactory {
    /**
     * Get the names this factory is registered under.
     *
     * @return The names.
     */
    override fun getNames() = listOf(
        "entity_effect"
    )

    /**
     * Create an entity effect particle from a key formatted as `<color>`.
     *
     * @param key The key.
     * @return The particle, or null if the key is invalid.
     */
    override fun create(key: String): SpawnableParticle? {
        val color = key.toParticleColor() ?: return null

        return SpawnableParticleEntityEffect(color)
    }

    /**
     * An entity effect particle with a fixed colour.
     */
    private class SpawnableParticleEntityEffect(
        private val color: Color
    ) : SpawnableParticle {
        /**
         * Spawn the particle at a location.
         *
         * @param location The location.
         * @param amount The amount to spawn.
         */
        override fun spawn(location: Location, amount: Int) {
            val world = location.world ?: return

            world.spawnParticle(Particle.ENTITY_EFFECT, location, amount, 0.0, 0.0, 0.0, 0.0, color)
        }
    }
}
