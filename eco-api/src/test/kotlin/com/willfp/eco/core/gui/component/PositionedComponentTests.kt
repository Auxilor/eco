package com.willfp.eco.core.gui.component

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.gui.slot.SlotBuilder
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.builder.ItemStackBuilder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
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

    private fun configButton(config: Config, enabledByDefault: Boolean, defaultItem: String? = "stone"): ConfigButton {
        mockkStatic(Items::class)
        every { Items.lookup(any()) } returns mockk<TestableItem> { every { item } returns mockk<ItemStack>(relaxed = true) }
        mockkConstructor(ItemStackBuilder::class)
        every { anyConstructed<ItemStackBuilder>().build() } returns mockk<ItemStack>()
        mockkStatic(Eco::class)
        val eco = mockk<Eco>()
        every { Eco.get() } returns eco
        every { eco.createSlotBuilder(any()) } returns mockk<SlotBuilder>(relaxed = true)

        return ConfigButton(config, defaultItem = defaultItem, enabledByDefault = enabledByDefault)
    }

    private fun enabledConfig(enabled: Boolean?) = mockk<Config>(relaxed = true) {
        every { getBoolOrNull("enabled") } returns enabled
        every { getStringOrNull(any()) } returns null
        every { has(any()) } returns false
        every { getSubsections("locations") } returns emptyList()
        every { getIntOrNull(any()) } returns 1
        every { getInt(any()) } returns 1
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

    @Test
    fun `config button enabled flag overrides the default`() {
        assertEquals(false, configButton(enabledConfig(false), enabledByDefault = true).isEnabled)
        assertEquals(true, configButton(enabledConfig(true), enabledByDefault = false).isEnabled)
        assertEquals(true, configButton(enabledConfig(null), enabledByDefault = true).isEnabled)
        assertEquals(false, configButton(enabledConfig(null), enabledByDefault = false).isEnabled)
    }

    @Test
    fun `config button without item or default item does not throw`() {
        configButton(enabledConfig(null), enabledByDefault = true, defaultItem = null)
    }

    private val airItem = mockk<ItemStack>(relaxed = true) { every { type } returns Material.AIR }

    private fun airButton(config: Config): SlotProvider {
        mockkStatic(Items::class)
        every { Items.lookup(any()) } returns mockk<TestableItem> { every { item } returns airItem }
        mockkConstructor(ItemStackBuilder::class)
        every { anyConstructed<ItemStackBuilder>().build() } returns mockk<ItemStack>()
        every { anyConstructed<ItemStackBuilder>().setDisplayName(any<String>()) } returns mockk<ItemStackBuilder>()
        mockkStatic(Eco::class)
        val eco = mockk<Eco>()
        every { Eco.get() } returns eco
        val provider = slot<SlotProvider>()
        every { eco.createSlotBuilder(capture(provider)) } returns mockk<SlotBuilder>(relaxed = true)

        ConfigButton(config)

        return provider.captured
    }

    @Test
    fun `config button without name or lore keeps an air item`() {
        val provider = airButton(enabledConfig(null))

        assertSame(airItem, provider.provide(mockk(), mockk()))
        verify(exactly = 0) { anyConstructed<ItemStackBuilder>().build() }
    }

    @Test
    fun `config button with a name builds an air item through the item builder`() {
        val config = enabledConfig(null)
        every { config.getStringOrNull("name") } returns "Name"

        airButton(config)

        verify(exactly = 1) { anyConstructed<ItemStackBuilder>().setDisplayName("Name") }
        verify(exactly = 1) { anyConstructed<ItemStackBuilder>().build() }
    }
}
