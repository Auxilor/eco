package com.willfp.eco.core.gui.slot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ConfigSlotTests {
    private val provider = slot<SlotProvider>()
    private val menu = mockk<Menu>()

    @BeforeEach
    fun mockEco() {
        val eco = mockk<Eco>(relaxed = true)
        every { eco.createSlotBuilder(capture(provider)) } returns mockk(relaxed = true)
        every { eco.translatePlaceholders(any(), any()) } answers {
            firstArg<String>().replace(
                oldValue = "%player%",
                newValue = secondArg<PlaceholderContext>().player!!.name
            )
        }

        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
        mockkStatic(Items::class)
    }

    @AfterEach
    fun unmockEco() {
        unmockkStatic(Items::class)
        unmockkStatic(Eco::class)
    }

    private fun stubLookup(lookup: String): ItemStack {
        val item = mockk<ItemStack>()
        val testable = mockk<TestableItem>()
        every { testable.item } returns item
        every { Items.lookup(lookup) } returns testable
        return item
    }

    private fun configWithItem(lookup: String): Config = mockk(relaxed = true) {
        every { getString("item") } returns lookup
        every { has("lore") } returns false
    }

    private fun playerNamed(playerName: String): Player {
        val player = mockk<Player>()
        every { player.name } returns playerName
        return player
    }

    @Test
    fun `head resolves per viewer`() {
        val aliceHead = stubLookup("player_head head:Alice")
        val bobHead = stubLookup("player_head head:Bob")

        ConfigSlot(configWithItem("player_head head:%player%"))

        assertSame(aliceHead, provider.captured.provide(playerNamed("Alice"), menu))
        assertSame(bobHead, provider.captured.provide(playerNamed("Bob"), menu))
    }

    @Test
    fun `resolved head is cached`() {
        val aliceHead = stubLookup("player_head head:Alice")

        ConfigSlot(configWithItem("player_head head:%player%"))

        assertSame(aliceHead, provider.captured.provide(playerNamed("Alice"), menu))
        assertSame(aliceHead, provider.captured.provide(playerNamed("Alice"), menu))
        verify(exactly = 1) { Items.lookup("player_head head:Alice") }
    }

    @Test
    fun `plain item looked up once`() {
        val stone = stubLookup("stone")

        ConfigSlot(configWithItem("stone"))

        assertSame(stone, provider.captured.provide(playerNamed("Alice"), menu))
        assertSame(stone, provider.captured.provide(playerNamed("Bob"), menu))
        verify(exactly = 1) { Items.lookup("stone") }
    }
}
