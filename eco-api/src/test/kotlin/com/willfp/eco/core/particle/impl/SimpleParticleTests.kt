package com.willfp.eco.core.particle.impl

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test

class SimpleParticleTests {
    private val world = mockk<World>(relaxed = true)
    private val location = mockk<Location>().also {
        every { it.world } returns world
    }

    /** A targeted particle is for one player only - leaking it to the world defeats the point. */
    @Test
    fun `spawning to a player does not spawn the particle for everyone`() {
        val player = mockk<Player>(relaxed = true)

        SimpleParticle(Particle.FLAME).spawnTo(player, location, 2)

        verify(exactly = 1) { player.spawnParticle(Particle.FLAME, location, 2, 0.0, 0.0, 0.0, 0.0) }
        verify(exactly = 0) { world.spawnParticle(any<Particle>(), any<Location>(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `spawning without a player still spawns for everyone`() {
        SimpleParticle(Particle.FLAME).spawn(location, 2)

        verify(exactly = 1) { world.spawnParticle(Particle.FLAME, location, 2, 0.0, 0.0, 0.0, 0.0) }
    }
}
