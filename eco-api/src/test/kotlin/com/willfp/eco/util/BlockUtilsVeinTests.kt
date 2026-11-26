package com.willfp.eco.util

import com.willfp.eco.core.Eco
import com.willfp.eco.core.blocks.TestableBlock
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.bukkit.Location
import org.bukkit.block.Block
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BlockUtilsVeinTests {
    private lateinit var eco: Eco

    @BeforeEach
    fun setUp() {
        eco = mockk(relaxed = true)
        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
        every { eco.isOwnedByCurrentRegion(any<Location>()) } returns true
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Eco::class)
    }

    /**
     * A chain of blocks where every face of block n leads to block n+1, and the last
     * block leads to itself, so the vein is as long as the limit allows.
     */
    private fun chain(length: Int): List<Block> {
        val blocks = (0 until length).map { mockk<Block>(relaxed = true) }

        for ((index, block) in blocks.withIndex()) {
            val next = blocks[(index + 1).coerceAtMost(length - 1)]
            every { block.getRelative(any<Int>(), any<Int>(), any<Int>()) } returns next
        }

        return blocks
    }

    private fun linkedBlocksAt(vararg positions: List<Int>): Map<List<Int>, Block> {
        val blocks = positions.associateWith { mockk<Block>(relaxed = true) }

        for ((position, block) in blocks) {
            every { block.getRelative(any<Int>(), any<Int>(), any<Int>()) } answers {
                val offset = listOf(firstArg<Int>(), secondArg<Int>(), thirdArg<Int>())
                blocks[position.zip(offset, Int::plus)] ?: mockk(relaxed = true)
            }
        }

        return blocks
    }

    private fun matchOnly(blocks: Collection<Block>): List<TestableBlock> = listOf(
        mockk(relaxed = true) {
            every { matches(any<Block>()) } answers { firstArg<Block>() in blocks }
        }
    )

    private fun matchAll(): List<TestableBlock> = listOf(
        mockk(relaxed = true) {
            every { matches(any<Block>()) } returns true
        }
    )

    @Test
    fun `the vein stops at the limit`() {
        val blocks = chain(10)

        val vein = BlockUtils.getVein(blocks.first(), matchAll(), 4)

        Assertions.assertEquals(4, vein.size)
    }

    @Test
    fun `the vein crosses chunks when every block is owned`() {
        val blocks = chain(6)

        val vein = BlockUtils.getVein(blocks.first(), matchAll(), 100)

        Assertions.assertEquals(6, vein.size)
    }

    @Test
    fun `a non-matching start yields nothing`() {
        val blocks = chain(4)
        val allowed = listOf<TestableBlock>(
            mockk(relaxed = true) {
                every { matches(any<Block>()) } returns false
            }
        )

        Assertions.assertTrue(BlockUtils.getVein(blocks.first(), allowed, 100).isEmpty())
    }

    @Test
    fun `the vein stops where the region stops`() {
        val blocks = chain(6)
        val boundary = blocks[3].location
        every { eco.isOwnedByCurrentRegion(boundary) } returns false

        val vein = BlockUtils.getVein(blocks.first(), matchAll(), 100)

        Assertions.assertEquals(3, vein.size)
    }

    @Test
    fun `edge diagonal joins vein`() {
        val blocks = linkedBlocksAt(listOf(0, 0, 0), listOf(1, 1, 0), listOf(1, 2, 0))

        val vein = BlockUtils.getVein(blocks.getValue(listOf(0, 0, 0)), matchOnly(blocks.values), 100)

        Assertions.assertEquals(blocks.values.toSet(), vein)
    }

    @Test
    fun `corner diagonal joins vein`() {
        val blocks = linkedBlocksAt(listOf(0, 0, 0), listOf(1, -1, 1))

        val vein = BlockUtils.getVein(blocks.getValue(listOf(0, 0, 0)), matchOnly(blocks.values), 100)

        Assertions.assertEquals(blocks.values.toSet(), vein)
    }

    @Test
    fun `horizontal offsets still join`() {
        val blocks = linkedBlocksAt(listOf(0, 0, 0), listOf(2, 0, 1))

        val vein = BlockUtils.getVein(blocks.getValue(listOf(0, 0, 0)), matchOnly(blocks.values), 100)

        Assertions.assertEquals(blocks.values.toSet(), vein)
    }

    @Test
    fun `one layer gap splits vein`() {
        val blocks = linkedBlocksAt(listOf(0, 0, 0), listOf(0, 2, 0))

        val vein = BlockUtils.getVein(blocks.getValue(listOf(0, 0, 0)), matchOnly(blocks.values), 100)

        Assertions.assertEquals(setOf(blocks.getValue(listOf(0, 0, 0))), vein)
    }
}
