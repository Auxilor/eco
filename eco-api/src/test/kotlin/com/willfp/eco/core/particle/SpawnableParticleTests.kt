package com.willfp.eco.core.particle

import io.mockk.mockk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SpawnableParticleTests {
    private val location = mockk<Location>()

    /**
     * The whole point of spawnTo having a default: particles written against the
     * older interface must keep spawning, just visible to everyone rather than to
     * the one player.
     */
    @Test
    fun `a particle that only implements world spawning still spawns when targeted at a player`() {
        val particle = WorldOnlyParticle()

        particle.spawnTo(mockk<Player>(), location, 3)

        assertEquals(listOf(location to 3), particle.spawns)
    }

    @Test
    fun `targeting a player without an amount spawns a single particle`() {
        val particle = WorldOnlyParticle()

        particle.spawnTo(mockk<Player>(), location)

        assertEquals(listOf(location to 1), particle.spawns)
    }

    @Test
    fun `targeting several players spawns once for each of them`() {
        val particle = TargetedParticle()
        val first = mockk<Player>()
        val second = mockk<Player>()

        particle.spawnTo(listOf(first, second), location, 2)

        assertEquals(listOf(first, second), particle.targets)
    }

    @Test
    fun `targeting no players spawns nothing`() {
        val particle = TargetedParticle()

        particle.spawnTo(emptyList(), location, 2)

        assertEquals(emptyList<Player>(), particle.targets)
    }

    /** Implements only the original interface method, as an older particle would. */
    private class WorldOnlyParticle : SpawnableParticle {
        val spawns = mutableListOf<Pair<Location, Int>>()

        override fun spawn(location: Location, amount: Int) {
            spawns += location to amount
        }
    }

    private class TargetedParticle : SpawnableParticle {
        val targets = mutableListOf<Player>()

        override fun spawn(location: Location, amount: Int) = Unit

        override fun spawnTo(player: Player, location: Location, amount: Int) {
            targets += player
        }
    }
}
