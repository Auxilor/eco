package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SlotAreaTests {
    private lateinit var config: Config

    @BeforeEach
    fun setup() {
        config = mockk()
    }

    private fun corners(topRow: Int, topColumn: Int, bottomRow: Int, bottomColumn: Int) {
        every { config.has("a.top-left") } returns true
        every { config.getInt("a.top-left.row") } returns topRow
        every { config.getInt("a.top-left.column") } returns topColumn
        every { config.getInt("a.bottom-right.row") } returns bottomRow
        every { config.getInt("a.bottom-right.column") } returns bottomColumn
    }

    @Test
    fun `box area from corners`() {
        corners(topRow = 2, topColumn = 2, bottomRow = 5, bottomColumn = 8)

        val area = config.getSlotArea("a")

        assertEquals(SlotArea(row = 2, column = 2, rows = 4, columns = 7), area)
        assertEquals(28, area.size)
    }

    @Test
    fun `anchored area from size`() {
        every { config.has("a.top-left") } returns false
        every { config.getInt("a.row") } returns 2
        every { config.getInt("a.column") } returns 1
        every { config.getInt("a.height") } returns 4
        every { config.getInt("a.width") } returns 9

        assertEquals(SlotArea(row = 2, column = 1, rows = 4, columns = 9), config.getSlotArea("a"))
    }

    @Test
    fun `inverted corners give no area`() {
        corners(topRow = 5, topColumn = 5, bottomRow = 2, bottomColumn = 2)

        assertEquals(0, config.getSlotArea("a").size)
    }
}
