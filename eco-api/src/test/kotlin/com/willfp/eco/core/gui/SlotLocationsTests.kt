package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SlotLocationsTests {
    private lateinit var config: Config

    @BeforeEach
    fun setup() {
        config = mockk()
    }

    private fun section(row: Int, column: Int): Config {
        val section = mockk<Config>()
        every { section.getInt("row") } returns row
        every { section.getInt("column") } returns column
        return section
    }

    @Test
    fun `locations list wins`() {
        every { config.getSubsections("a.locations") } returns listOf(
            section(row = 1, column = 1),
            section(row = 6, column = 9)
        )

        assertEquals(
            listOf(SlotLocation(row = 1, column = 1), SlotLocation(row = 6, column = 9)),
            config.getSlotLocations("a")
        )
    }

    @Test
    fun `location section is read`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.getIntOrNull("a.location.row") } returns 6
        every { config.getIntOrNull("a.location.column") } returns 5

        assertEquals(listOf(SlotLocation(row = 6, column = 5)), config.getSlotLocations("a"))
    }

    @Test
    fun `flat row and column are read`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.getIntOrNull("a.location.row") } returns null
        every { config.getIntOrNull("a.location.column") } returns null
        every { config.getInt("a.row") } returns 2
        every { config.getInt("a.column") } returns 3

        assertEquals(listOf(SlotLocation(row = 2, column = 3)), config.getSlotLocations("a"))
    }

    @Test
    fun `default used when nothing set`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.has("a.location.row") } returns false
        every { config.has("a.row") } returns false

        assertEquals(
            listOf(SlotLocation(row = 6, column = 1)),
            config.getSlotLocations("a", listOf(SlotLocation(row = 6, column = 1)))
        )
    }

    @Test
    fun `default ignored when set`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.has("a.location.row") } returns false
        every { config.has("a.row") } returns true
        every { config.getIntOrNull("a.location.row") } returns null
        every { config.getIntOrNull("a.location.column") } returns null
        every { config.getInt("a.row") } returns 3
        every { config.getInt("a.column") } returns 4

        assertEquals(
            listOf(SlotLocation(row = 3, column = 4)),
            config.getSlotLocations("a", listOf(SlotLocation(row = 6, column = 1)))
        )
    }
}
