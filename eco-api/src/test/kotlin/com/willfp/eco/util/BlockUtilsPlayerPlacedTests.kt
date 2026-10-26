package com.willfp.eco.util

import com.willfp.eco.core.Eco
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

internal class BlockUtilsPlayerPlacedTests {
    private val storedKeys = ConcurrentHashMap.newKeySet<NamespacedKey>()

    private val container = mockk<PersistentDataContainer> {
        every { has(any(), PersistentDataType.INTEGER) } answers { call -> call.invocation.args.first() in storedKeys }
    }

    private val chunk = mockk<Chunk>(relaxed = true) {
        every { persistentDataContainer } returns container
    }

    @BeforeEach
    fun setUp() {
        val eco = mockk<Eco>(relaxed = true)
        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
        every { eco.createNamespacedKey(any(), any()) } answers { call ->
            key(call.invocation.args.joinToString(":"))
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Eco::class)
    }

    private fun key(value: String): NamespacedKey = requireNotNull(NamespacedKey.fromString(value))

    private fun block(x: Int, y: Int, z: Int): Block {
        val block = mockk<Block>(relaxed = true)

        every { block.x } returns x
        every { block.y } returns y
        every { block.z } returns z
        every { block.location } returns Location(null, x.toDouble(), y.toDouble(), z.toDouble())
        every { block.chunk } returns chunk

        return block
    }

    @Test
    fun `colliding hashes stay apart`() {
        val placed = block(x = 0, y = 0, z = 2)
        val natural = block(x = 0, y = -2, z = 0)
        Assertions.assertEquals(placed.location.hashCode(), natural.location.hashCode())

        storedKeys += key("eco:placed_0_0_2")

        Assertions.assertTrue(BlockUtils.isPlayerPlaced(placed))
        Assertions.assertFalse(BlockUtils.isPlayerPlaced(natural))
    }

    @Test
    fun `negative coordinates are keyed`() {
        storedKeys += key("eco:placed_15_-64_1")

        Assertions.assertTrue(BlockUtils.isPlayerPlaced(block(x = -1, y = -64, z = -15)))
        Assertions.assertFalse(BlockUtils.isPlayerPlaced(block(x = -1, y = -63, z = -15)))
    }

    @Test
    fun `legacy markers are still read`() {
        val placed = block(x = 3, y = 70, z = 9)

        storedKeys += key("eco:" + placed.location.hashCode().toString(16))

        Assertions.assertTrue(BlockUtils.isPlayerPlaced(placed))
    }
}
