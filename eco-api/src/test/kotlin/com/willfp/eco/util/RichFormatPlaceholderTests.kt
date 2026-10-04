package com.willfp.eco.util

import com.willfp.eco.core.Eco
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RichFormatPlaceholderTests {
    @BeforeAll
    fun mockEco() {
        val eco = mockk<Eco>(relaxed = true)
        every { eco.translatePlaceholders(any(), any()) } answers {
            firstArg<String>()
                .replace("%nick%", "<click:run_command:'/op me'>&cNick")
                .replace("%rank%", "&cAdmin")
        }

        mockkStatic(Eco::class)
        every { Eco.get() } returns eco
    }

    @AfterAll
    fun unmockEco() {
        unmockkStatic(Eco::class)
    }

    private fun Component.flatten(): List<Component> =
        listOf(this) + children().flatMap { it.flatten() }

    @Test
    fun `tags in placeholder values are not parsed`() {
        val component = StringUtils.formatToRichComponent("Hi %nick%", PlaceholderContext.EMPTY)

        assertNull(component.flatten().firstNotNullOfOrNull { it.clickEvent() })
        assertEquals(
            "Hi <click:run_command:'/op me'>Nick",
            StringUtils.toLegacy(component).replace("§c", "")
        )
    }

    @Test
    fun `legacy colours in placeholder values still apply`() {
        val component = StringUtils.formatToRichComponent("%nick%", PlaceholderContext.EMPTY)

        assertEquals(
            NamedTextColor.RED,
            component.flatten().firstNotNullOfOrNull { it.color() }
        )
    }

    @Test
    fun `legacy colours in placeholder values stay in the value`() {
        val component = StringUtils.formatToRichComponent("%rank% <green>Name", PlaceholderContext.EMPTY)

        assertEquals("§cAdmin§r §aName", StringUtils.toLegacy(component))
    }

    @Test
    fun `placeholder values take the surrounding style`() {
        val component = StringUtils.formatToRichComponent("<bold>Hi %nick%", PlaceholderContext.EMPTY)

        assertEquals(
            "§lHi <click:run_command:'/op me'>§c§lNick",
            StringUtils.toLegacy(component)
        )
    }
}
