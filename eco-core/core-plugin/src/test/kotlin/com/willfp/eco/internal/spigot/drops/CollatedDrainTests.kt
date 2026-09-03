package com.willfp.eco.internal.spigot.drops

import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue.CollatedDrops
import io.mockk.mockk
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Location
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CollatedDrainTests {
    private fun drops(): CollatedDrops =
        CollatedDrops(mutableListOf(), mockk<Location>(relaxed = true), 0, false)

    @Test
    fun `every pending entry is dispatched`() {
        val map = ConcurrentHashMap<Player, CollatedDrops>()
        val first = mockk<Player>(relaxed = true)
        val second = mockk<Player>(relaxed = true)
        map[first] = drops()
        map[second] = drops()

        val dispatched = mutableListOf<Player>()
        drainCollatedDrops(map) { player, _ -> dispatched.add(player) }

        Assertions.assertEquals(2, dispatched.size)
        Assertions.assertTrue(dispatched.containsAll(listOf(first, second)))
    }

    @Test
    fun `dispatched entries are removed`() {
        val map = ConcurrentHashMap<Player, CollatedDrops>()
        map[mockk<Player>(relaxed = true)] = drops()

        drainCollatedDrops(map) { _, _ -> }

        Assertions.assertTrue(map.isEmpty())
    }

    @Test
    fun `an entry added during the drain survives it`() {
        val map = ConcurrentHashMap<Player, CollatedDrops>()
        map[mockk<Player>(relaxed = true)] = drops()
        val late = mockk<Player>(relaxed = true)

        drainCollatedDrops(map) { _, _ -> map[late] = drops() }

        Assertions.assertTrue(map.containsKey(late))
        Assertions.assertEquals(1, map.size)
    }

    @Test
    fun `an entry is never dispatched twice`() {
        val map = ConcurrentHashMap<Player, CollatedDrops>()
        val player = mockk<Player>(relaxed = true)
        map[player] = drops()

        var count = 0
        drainCollatedDrops(map) { _, _ -> count++ }
        drainCollatedDrops(map) { _, _ -> count++ }

        Assertions.assertEquals(1, count)
    }
}
