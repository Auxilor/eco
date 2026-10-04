package com.willfp.eco.core.display

import com.willfp.eco.core.EcoPlugin
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DisplayModuleRegistryTests {
    private val plugin = mockk<EcoPlugin>(relaxed = true)

    private fun module(weight: Int) = object : DisplayModule(plugin, weight) {}

    @Test
    fun `modules are ordered by weight then registration order`() {
        val registry = DisplayModuleRegistry()
        val high = module(500)
        val lowFirst = module(100)
        val lowSecond = module(100)

        registry.register(high)
        registry.register(lowFirst)
        registry.register(lowSecond)

        assertEquals(listOf(lowFirst, lowSecond, high), registry.modules)
    }

    @Test
    fun `unregistering removes only that module`() {
        val registry = DisplayModuleRegistry()
        val kept = module(100)
        val removed = module(200)

        registry.register(kept)
        registry.register(removed)
        registry.unregister(removed)

        assertEquals(listOf(kept), registry.modules)
    }

    @Test
    fun `a snapshot is not changed by later registrations`() {
        val registry = DisplayModuleRegistry()
        val first = module(100)

        registry.register(first)
        val snapshot = registry.modules
        registry.register(module(200))

        assertEquals(listOf(first), snapshot)
    }
}
