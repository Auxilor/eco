package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler
import io.papermc.paper.threadedregions.scheduler.RegionScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Bukkit
import org.bukkit.World
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FoliaTaskContextTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var global: GlobalRegionScheduler
    private lateinit var region: RegionScheduler
    private lateinit var world: World
    private val registry = ConcurrentHashMap.newKeySet<FoliaEcoTask>()
    private val runnable = Runnable { }

    @BeforeEach
    fun setUp() {
        plugin = mockk(relaxed = true)
        global = mockk(relaxed = true)
        region = mockk(relaxed = true)
        world = mockk(relaxed = true)
        registry.clear()
        mockkStatic(Bukkit::class)
        every { Bukkit.getGlobalRegionScheduler() } returns global
        every { Bukkit.getRegionScheduler() } returns region
        every { global.run(any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { global.runDelayed(any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { global.runAtFixedRate(any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { region.run(any(), any<World>(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { region.runDelayed(any(), any<World>(), any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { region.runAtFixedRate(any(), any<World>(), any(), any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    private fun globalContext() = FoliaGlobalTaskContext(plugin, registry)

    private fun regionContext() = FoliaRegionTaskContext(plugin, registry, world, 3, 7)

    @Test
    fun `global run uses the global scheduler`() {
        globalContext().run(runnable)

        verify { global.run(plugin, any()) }
    }

    @Test
    fun `global run later uses a delayed task`() {
        globalContext().runLater(runnable, 20L)

        verify { global.runDelayed(plugin, any(), 20L) }
    }

    @Test
    fun `a delay of zero becomes a next tick task`() {
        globalContext().runLater(runnable, 0L)

        verify { global.run(plugin, any()) }
        verify(exactly = 0) { global.runDelayed(any(), any(), any()) }
    }

    @Test
    fun `a negative delay becomes a next tick task`() {
        globalContext().runLater(runnable, -5L)

        verify { global.run(plugin, any()) }
    }

    @Test
    fun `global run timer uses a fixed rate task`() {
        globalContext().runTimer(runnable, 5L, 20L)

        verify { global.runAtFixedRate(plugin, any(), 5L, 20L) }
    }

    @Test
    fun `a timer delay of zero is raised to one`() {
        globalContext().runTimer(runnable, 0L, 20L)

        verify { global.runAtFixedRate(plugin, any(), 1L, 20L) }
    }

    @Test
    fun `a timer period below one is raised to one`() {
        globalContext().runTimer(runnable, 5L, 0L)

        verify { global.runAtFixedRate(plugin, any(), 5L, 1L) }
    }

    @Test
    fun `region run targets the chunk`() {
        regionContext().run(runnable)

        verify { region.run(plugin, world, 3, 7, any()) }
    }

    @Test
    fun `region run later targets the chunk`() {
        regionContext().runLater(runnable, 20L)

        verify { region.runDelayed(plugin, world, 3, 7, any(), 20L) }
    }

    @Test
    fun `region delay of zero becomes a next tick task`() {
        regionContext().runLater(runnable, 0L)

        verify { region.run(plugin, world, 3, 7, any()) }
    }

    @Test
    fun `region run timer targets the chunk`() {
        regionContext().runTimer(runnable, 5L, 20L)

        verify { region.runAtFixedRate(plugin, world, 3, 7, any(), 5L, 20L) }
    }

    @Test
    fun `submitted tasks are registered`() {
        globalContext().runTimer(runnable, 5L, 20L)

        org.junit.jupiter.api.Assertions.assertEquals(1, registry.size)
    }
}
