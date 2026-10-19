package com.willfp.eco.core.gui.component

import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.slot.Slot
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class PositionedComponentTests {
    private val builder = mockk<MenuBuilder>(relaxed = true)

    private class StubComponent(
        override val locations: List<SlotLocation>,
        override val isEnabled: Boolean = true
    ) : PositionedComponent {
        override fun getSlotAt(row: Int, column: Int): Slot? = null
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `component is added at every location`() {
        val component = StubComponent(listOf(SlotLocation(6, 1), SlotLocation(6, 9)))

        builder.addComponent(component)

        verify(exactly = 1) { builder.addComponent(MenuLayer.MIDDLE, 6, 1, component) }
        verify(exactly = 1) { builder.addComponent(MenuLayer.MIDDLE, 6, 9, component) }
    }

    @Test
    fun `disabled component is not added`() {
        builder.addComponent(StubComponent(listOf(SlotLocation(6, 1)), isEnabled = false))

        verify(exactly = 0) { builder.addComponent(any<Int>(), any(), any(), any()) }
    }

    @Test
    fun `explicit layer wins`() {
        val component = StubComponent(listOf(SlotLocation(2, 3)))

        builder.addComponent(MenuLayer.LOWER, component)

        verify(exactly = 1) { builder.addComponent(MenuLayer.LOWER, 2, 3, component) }
    }
}
