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
        every { config.getSubsections("a.locations") } returns listOf(section(1, 1), section(6, 9))

        assertEquals(listOf(SlotLocation(1, 1), SlotLocation(6, 9)), config.getSlotLocations("a"))
    }

    @Test
    fun `location section is read`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.getIntOrNull("a.location.row") } returns 6
        every { config.getIntOrNull("a.location.column") } returns 5

        assertEquals(listOf(SlotLocation(6, 5)), config.getSlotLocations("a"))
    }

    @Test
    fun `flat row and column are read`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.getIntOrNull("a.location.row") } returns null
        every { config.getIntOrNull("a.location.column") } returns null
        every { config.getInt("a.row") } returns 2
        every { config.getInt("a.column") } returns 3

        assertEquals(listOf(SlotLocation(2, 3)), config.getSlotLocations("a"))
    }

    @Test
    fun `getSlotLocations returns the default when nothing is set`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.has("a.location.row") } returns false
        every { config.has("a.row") } returns false

        assertEquals(
            listOf(SlotLocation(6, 1)),
            config.getSlotLocations("a", listOf(SlotLocation(6, 1)))
        )
    }

    @Test
    fun `default is ignored when a location is set`() {
        every { config.getSubsections("a.locations") } returns emptyList()
        every { config.has("a.location.row") } returns false
        every { config.has("a.row") } returns true
        every { config.getIntOrNull("a.location.row") } returns null
        every { config.getIntOrNull("a.location.column") } returns null
        every { config.getInt("a.row") } returns 3
        every { config.getInt("a.column") } returns 4

        assertEquals(
            listOf(SlotLocation(3, 4)),
            config.getSlotLocations("a", listOf(SlotLocation(6, 1)))
        )
    }
}
