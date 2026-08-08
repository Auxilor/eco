package com.willfp.eco.internal.particle

import com.willfp.eco.core.particle.ParticleFactory
import com.willfp.eco.core.particle.SpawnableParticle
import org.bukkit.Location
import org.bukkit.Particle

/**
 * Creates dust particles that fade between two colours, e.g. `dust_transition:ff0000:0000ff`.
 */
object ParticleFactoryDustTransition : ParticleFactory {
    /**
     * Get the names this factory is registered under.
     *
     * @return The names.
     */
    override fun getNames() = listOf(
        "dust_transition",
        "transition"
    )

    /**
     * Create a transitioning dust particle from a key formatted as `<from>:<to>` or
     * `<from>:<to>:<size>`.
     *
     * @param key The key.
     * @return The particle, or null if the key is invalid.
     */
    override fun create(key: String): SpawnableParticle? {
        val parts = key.split(":")

        if (parts.size !in 2..3) {
            return null
        }

        val from = parts[0].toParticleColor() ?: return null
        val to = parts[1].toParticleColor() ?: return null
        val size = parts.getOrNull(2).toParticleSize() ?: return null

        return SpawnableParticleDustTransition(Particle.DustTransition(from, to, size))
    }

    /**
     * A dust particle that fades between two fixed colours.
     */
    private class SpawnableParticleDustTransition(
        private val options: Particle.DustTransition
    ) : SpawnableParticle {
        /**
         * Spawn the particle at a location.
         *
         * @param location The location.
         * @param amount The amount to spawn.
         */
        override fun spawn(location: Location, amount: Int) {
            val world = location.world ?: return

            world.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, amount, 0.0, 0.0, 0.0, 0.0, options)
        }
    }
}
