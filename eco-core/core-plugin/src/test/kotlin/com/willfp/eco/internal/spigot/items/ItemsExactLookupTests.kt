package com.willfp.eco.internal.spigot.items

import com.willfp.eco.core.Eco
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ItemsExactLookupTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            mockkStatic(Eco::class)
            every { Eco.get() } returns mockk(relaxed = true)
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    @Test
    fun `wrapped item keeps its key`() {
        val stack = mockk<ItemStack>(relaxed = true) {
            every { hasItemMeta() } returns true
            every { amount } returns 1
        }
        val key = NamespacedKey("test", "registered_item")
        val testable = mockk<TestableItem> {
            every { shouldMarkAsCustom() } returns true
            every { matches(any()) } returns true
            every { item } returns stack
        }

        Items.registerCustomItem(key, testable)

        try {
            assertEquals("test:registered_item", Items.toExactLookupString(stack))
        } finally {
            Items.removeCustomItem(key)
        }
    }
}
