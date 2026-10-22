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
import java.util.concurrent.CopyOnWriteArrayList

internal class LevelComponentTests {
    private lateinit var player: Player
    private lateinit var menu: Menu
    private val requestedLevels = CopyOnWriteArrayList<Int>()

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
    fun `pattern maps to level offsets`() {
        assertEquals(
            mapOf(
                SlotLocation(row = 1, column = 2) to 1,
                SlotLocation(row = 1, column = 3) to 2,
                SlotLocation(row = 2, column = 1) to 10,
                SlotLocation(row = 2, column = 4) to 11
            ),
            parseProgressionPattern(listOf("0120", "a00b"))
        )
    }

    @Test
    fun `empty pattern has no pages`() {
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
    fun `page two shows offset level`() {
        val component = component(listOf("1234567"), 20)
        every { menu.getPage(player) } returns 2

        component.getSlotAt(row = 1, column = 3, player = player, menu = menu)!!.getItemStack(player)

        assertEquals(listOf(10), requestedLevels)
    }

    @Test
    fun `levels past max have no slot`() {
        val component = component(listOf("1234567"), 8)
        every { menu.getPage(player) } returns 2

        assertNotNull(component.getSlotAt(row = 1, column = 1, player = player, menu = menu))
        assertNull(component.getSlotAt(row = 1, column = 3, player = player, menu = menu))
    }

    @Test
    fun `level state of`() {
        assertEquals(LevelState.UNLOCKED, LevelState.of(level = 3, currentLevel = 3))
        assertEquals(LevelState.IN_PROGRESS, LevelState.of(level = 4, currentLevel = 3))
        assertEquals(LevelState.LOCKED, LevelState.of(level = 5, currentLevel = 3))
    }
}
