package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.SlotArea
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.Slot
import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PagedAreaComponentTests {
    private lateinit var player: Player
    private lateinit var menu: Menu
    private val emptySlot = mockk<Slot>()

    private class TestArea(area: SlotArea, private val entries: List<Int>, private val empty: Slot) :
        PagedAreaComponent<Int>(area) {
        override fun getEntries(player: Player, menu: Menu) = entries

        override fun getEntrySlot(player: Player, menu: Menu, entry: Int): Slot? = null

        override fun getEmptySlot(player: Player, menu: Menu) = empty
    }

    @BeforeEach
    fun setup() {
        player = mockk()
        menu = mockk()
    }

    private fun component(area: SlotArea, entryCount: Int) =
        TestArea(area, List(entryCount) { it }, emptySlot)

    @Test
    fun `entry index follows page`() {
        every { menu.getPage(player) } returns 2

        assertEquals(
            7,
            component(SlotArea(row = 1, column = 1, rows = 2, columns = 3), 10)
                .getEntryAt(player, menu, row = 1, column = 2)
        )
    }

    @Test
    fun `missing entry uses empty slot`() {
        every { menu.getPage(player) } returns 2

        assertSame(
            emptySlot,
            component(SlotArea(row = 1, column = 1, rows = 2, columns = 3), 10)
                .getSlotAt(row = 2, column = 3, player = player, menu = menu)
        )
    }

    @Test
    fun `empty entries give zero pages`() {
        assertEquals(0, component(SlotArea(row = 1, column = 1, rows = 2, columns = 3), 0).getPages(player, menu))
    }

    @Test
    fun `empty area gives zero pages`() {
        assertEquals(0, component(SlotArea(row = 1, column = 1, rows = 0, columns = 0), 5).getPages(player, menu))
    }

    @Test
    fun `pages round up`() {
        assertEquals(3, component(SlotArea(row = 1, column = 1, rows = 2, columns = 3), 13).getPages(player, menu))
    }
}
