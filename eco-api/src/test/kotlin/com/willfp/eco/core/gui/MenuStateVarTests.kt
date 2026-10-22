package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val Menu.delegatedState by menuStateVar<String>("k")

internal class MenuStateVarTests {
    private lateinit var menu: Menu
    private lateinit var player: Player

    @BeforeEach
    fun setup() {
        menu = mockk(relaxed = true)
        player = mockk()
    }

    @Test
    fun `nullable var reads and writes`() {
        every { menu.getState<String>(player, "k") } returns "v"
        val stateVar = menuStateVar<String>(menu, "k")

        assertEquals("v", stateVar[player])

        stateVar[player] = "w"

        verify { menu.setState(player, "k", "w") }
    }

    @Test
    fun `unset var returns default`() {
        every { menu.getState<Int>(player, "k") } returns null

        assertEquals(1, menuStateVar(menu, "k", 1)[player])
    }

    @Test
    fun `delegate is cached per menu`() {
        assertSame(menu.delegatedState, menu.delegatedState)
    }
}
