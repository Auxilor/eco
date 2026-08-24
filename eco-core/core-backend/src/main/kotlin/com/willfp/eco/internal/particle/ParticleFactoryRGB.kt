package com.willfp.eco.internal.particle

import com.willfp.eco.core.particle.ParticleFactory
import com.willfp.eco.core.particle.SpawnableParticle
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player

/**
 * Creates coloured dust particles, e.g. `rgb:00ff00` or `dust:ff0000:3`.
 */
object ParticleFactoryRGB : ParticleFactory {
    /**
     * Get the names this factory is registered under.
     *
     * @return The names.
     */
    override fun getNames() = listOf(
        "color",
        "rgb",
        "hex",
        "dust"
    )

    /**
     * Create a dust particle from a key formatted as `<color>` or `<color>:<size>`.
     *
     * @param key The key.
     * @return The particle, or null if the key is invalid.
     */
    override fun create(key: String): SpawnableParticle? {
        val parts = key.split(":")

        if (parts.size !in 1..2) {
            return null
        }

        val color = parts[0].toParticleColor() ?: return null
        val size = parts.getOrNull(1).toParticleSize() ?: return null

        return SpawnableParticleRGB(Particle.DustOptions(color, size))
    }

    /**
     * A dust particle with a fixed colour and size.
     */
    private class SpawnableParticleRGB(
        private val options: Particle.DustOptions
    ) : SpawnableParticle {
        /**
         * Spawn the particle at a location.
         *
         * @param location The location.
         * @param amount The amount to spawn.
         */
        override fun spawn(location: Location, amount: Int) {
            val world = location.world ?: return

            world.spawnParticle(Particle.DUST, location, amount, 0.0, 0.0, 0.0, 0.0, options)
        }

        /**
         * Spawn the particle at a location, visible only to a single player.
         *
         * @param player The player to spawn the particle for.
         * @param location The location.
         * @param amount The amount to spawn.
         */
        override fun spawnTo(player: Player, location: Location, amount: Int) {
            player.spawnParticle(Particle.DUST, location, amount, 0.0, 0.0, 0.0, 0.0, options)
        }
    }
}
