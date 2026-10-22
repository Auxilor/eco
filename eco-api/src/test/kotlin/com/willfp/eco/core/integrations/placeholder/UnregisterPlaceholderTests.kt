package com.willfp.eco.core.integrations.placeholder

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.base.ConfigYml
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class UnregisterPlaceholderTests {
    companion object {
        @JvmStatic
        @BeforeAll
        fun stubEco() {
            val configYml = mockk<ConfigYml>()
            every { configYml.getInt("literal-cache-ttl") } returns 10

            val ecoPlugin = mockk<EcoPlugin>()
            every { ecoPlugin.configYml } returns configYml

            val eco = mockk<Eco>()
            every { eco.ecoPlugin } returns ecoPlugin

            mockkStatic(Eco::class)
            every { Eco.get() } returns eco
        }

        @JvmStatic
        @AfterAll
        fun unstubEco() {
            unmockkStatic(Eco::class)
        }
    }

    private val plugin = mockk<EcoPlugin>()

    @Test
    fun `unregister removes placeholder`() {
        val placeholder = PlayerlessPlaceholder(plugin, "unregister_removed") { "value" }.register()

        placeholder.unregister()

        assertNull(PlaceholderManager.getRegisteredPlaceholder(plugin, "unregister_removed"))
        assert(PlaceholderManager.getRegisteredPlaceholders(plugin).none { it === placeholder })
    }

    @Test
    fun `unregister keeps replacement`() {
        val old = PlayerlessPlaceholder(plugin, "unregister_replaced") { "old" }.register()
        val replacement = PlayerlessPlaceholder(plugin, "unregister_replaced") { "new" }.register()

        old.unregister()

        assertSame(replacement, PlaceholderManager.getRegisteredPlaceholder(plugin, "unregister_replaced"))
    }
}
