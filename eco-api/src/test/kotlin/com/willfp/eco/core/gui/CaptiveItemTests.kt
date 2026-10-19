package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CaptiveItemTests {
    @Test
    fun `unbound captive item returns null`() {
        assertNull(CaptiveItem()[mockk<Player>()])
    }

    @Test
    fun `bound captive item reads the menu`() {
        val player = mockk<Player>()
        val stack = mockk<ItemStack>()
        val menu = mockk<Menu>()
        every { menu.getCaptiveItem(player, 2, 3) } returns stack
        val captiveItem = CaptiveItem()

        captiveItem.bind(menu, 2, 3)

        assertSame(stack, captiveItem[player])
    }

    @Test
    fun `binding twice fails`() {
        val captiveItem = CaptiveItem()
        captiveItem.bind(mockk(), 1, 1)

        assertThrows<IllegalArgumentException> { captiveItem.bind(mockk(), 1, 1) }
    }
}
