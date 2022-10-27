package com.willfp.eco.internal.particle

import com.willfp.eco.core.particle.ParticleFactory
import com.willfp.eco.core.particle.SpawnableParticle
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle

object ParticleFactoryRGB : ParticleFactory {
    override fun getNames() = listOf(
        "color",
        "rgb",
        "hex"
    )

    override fun create(key: String): SpawnableParticle? {
        val hex = key.toIntOrNull(16) ?: return null
        val color = try {
            Color.fromRGB(hex)
        } catch (e: IllegalArgumentException) {
            return null
        }

        return SpawnableParticleRGB(Particle.DustOptions(color, 1.0f))
    }

    private class SpawnableParticleRGB(
        private val options: Particle.DustOptions
    ) : SpawnableParticle {
        override fun spawn(location: Location, amount: Int) {
            val world = location.world ?: return

            world.spawnParticle(Particle.REDSTONE, location, amount, 0.0, 0.0, 0.0, 0.0, options)
        }
    }
}
