package com.willfp.eco.core.gui.component

import com.willfp.eco.core.Eco
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LevelComponentTests {
    private lateinit var player: Player
    private lateinit var menu: Menu
    private val requestedLevels = mutableListOf<Int>()

    @BeforeEach
    fun setup() {
        player = mockk()
        menu = mockk()
        mockkStatic(Eco::class)
        val eco = mockk<Eco>()
        every { Eco.get() } returns eco
        every { eco.createSlotBuilder(any()) } answers {
            val provider = firstArg<SlotProvider>()
            val builder = mockk<SlotBuilder>()
            val slot = mockk<Slot>()
            every { slot.getItemStack(any()) } answers { provider.provide(firstArg(), menu)!! }
            every { builder.onLeftClick(any<SlotHandler>()) } returns builder
            every { builder.build() } returns slot
            builder
        }
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Eco::class)
    }

    private fun component(rows: List<String>, max: Int) = object : LevelComponent() {
        override val pattern = rows
        override val maxLevel = max

        override fun getLevelItem(player: Player, menu: Menu, level: Int, levelState: LevelState): ItemStack {
            requestedLevels.add(level)
            return mockk()
        }

        override fun getLevelState(player: Player, level: Int) = LevelState.LOCKED
    }

    @Test
    fun `pattern maps characters to level offsets`() {
        assertEquals(
            mapOf(
                SlotLocation(1, 2) to 1,
                SlotLocation(1, 3) to 2,
                SlotLocation(2, 1) to 10,
                SlotLocation(2, 4) to 11
            ),
            parseProgressionPattern(listOf("0120", "a00b"))
        )
    }

    @Test
    fun `pattern without levels has no pages`() {
        val component = component(listOf("000"), 10)

        assertEquals(0, component.levelsPerPage)
        assertEquals(0, component.pages)
        assertEquals(0, component.getPageOf(5))
        every { menu.getPage(player) } returns 1
        assertNull(component.getSlotAt(1, 1, player, menu))
    }

    @Test
    fun `pages round up`() {
        val component = component(listOf("1234567"), 20)

        assertEquals(3, component.pages)
        assertEquals(2, component.getPageOf(14))
    }

    @Test
    fun `slot on page two shows the offset level`() {
        val component = component(listOf("1234567"), 20)
        every { menu.getPage(player) } returns 2

        component.getSlotAt(1, 3, player, menu)!!.getItemStack(player)

        assertEquals(listOf(10), requestedLevels)
    }

    @Test
    fun `levels past max have no slot`() {
        val component = component(listOf("1234567"), 8)
        every { menu.getPage(player) } returns 2

        assertNotNull(component.getSlotAt(1, 1, player, menu))
        assertNull(component.getSlotAt(1, 3, player, menu))
    }

    @Test
    fun `level state of`() {
        assertEquals(LevelState.UNLOCKED, LevelState.of(3, 3))
        assertEquals(LevelState.IN_PROGRESS, LevelState.of(4, 3))
        assertEquals(LevelState.LOCKED, LevelState.of(5, 3))
    }
}
