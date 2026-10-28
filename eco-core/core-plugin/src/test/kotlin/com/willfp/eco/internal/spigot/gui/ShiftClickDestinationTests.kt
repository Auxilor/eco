package com.willfp.eco.internal.spigot.gui

import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * A shift-click fills partial stacks of the same item before the first empty slot, so those are
 * the slots a menu has to check, not just the first empty one.
 */
class ShiftClickDestinationTests {
    private val moved = stackOf(Material.DIAMOND, 10)

    @Test
    fun `partial stacks come first`() {
        val inventory = inventoryOf(stackOf(Material.STONE, 1), null, stackOf(Material.DIAMOND, 60))

        assertEquals(listOf(2, 1), inventory.shiftClickDestinations(moved))
    }

    @Test
    fun `a partial stack can take all`() {
        val inventory = inventoryOf(null, stackOf(Material.DIAMOND, 30))

        assertEquals(listOf(1), inventory.shiftClickDestinations(moved))
    }

    @Test
    fun `a full menu has no destination`() {
        val inventory = inventoryOf(stackOf(Material.STONE, 1), stackOf(Material.DIAMOND, 64))

        assertEquals(emptyList<Int>(), inventory.shiftClickDestinations(moved))
    }

    @Test
    fun `the menu stack limit applies`() {
        val inventory = inventoryOf(stackOf(Material.DIAMOND, 30), null, maxStackSize = 32)

        assertEquals(listOf(0, 1), inventory.shiftClickDestinations(moved))
    }

    @Test
    fun `overflow fills more empties`() {
        val inventory = inventoryOf(null, null, null, maxStackSize = 4)

        assertEquals(listOf(0, 1, 2), inventory.shiftClickDestinations(moved))
    }

    private fun inventoryOf(vararg contents: ItemStack?, maxStackSize: Int = 64): Inventory {
        val inventory = mockk<Inventory>()
        every { inventory.contents } returns arrayOf(*contents)
        every { inventory.maxStackSize } returns maxStackSize
        return inventory
    }

    private fun stackOf(material: Material, amount: Int): ItemStack {
        val stack = mockk<ItemStack>()
        every { stack.type } returns material
        every { stack.amount } returns amount
        every { stack.maxStackSize } returns 64
        every { stack.isSimilar(any()) } answers { firstArg<ItemStack?>()?.type == material }
        return stack
    }
}
