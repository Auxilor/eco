package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler
import io.papermc.paper.threadedregions.scheduler.RegionScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Suppress("DEPRECATION")
internal class EcoSchedulerFoliaTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var global: GlobalRegionScheduler
    private lateinit var region: RegionScheduler
    private lateinit var async: AsyncScheduler
    private lateinit var world: World

    @BeforeEach
    fun setUp() {
        plugin = mockk(relaxed = true)
        global = mockk(relaxed = true)
        region = mockk(relaxed = true)
        async = mockk(relaxed = true)
        world = mockk(relaxed = true)
        mockkStatic(Bukkit::class)
        every { Bukkit.getGlobalRegionScheduler() } returns global
        every { Bukkit.getRegionScheduler() } returns region
        every { Bukkit.getAsyncScheduler() } returns async
        every { global.run(any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { global.runAtFixedRate(any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { region.run(any(), any<World>(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    @Test
    fun `a location resolves to its chunk`() {
        val location = mockk<Location>(relaxed = true)
        every { location.world } returns world
        every { location.blockX } returns 35
        every { location.blockZ } returns -20

        EcoSchedulerFolia(plugin).at(location).run { }

        verify { region.run(plugin, world, 2, -2, any()) }
    }

    @Test
    fun `chunk coordinates are used directly`() {
        EcoSchedulerFolia(plugin).at(world, 4, 9).run { }

        verify { region.run(plugin, world, 4, 9, any()) }
    }

    @Test
    fun `the global context is reused`() {
        val scheduler = EcoSchedulerFolia(plugin)

        Assertions.assertSame(scheduler.global(), scheduler.global())
    }

    @Test
    fun `the async context is reused`() {
        val scheduler = EcoSchedulerFolia(plugin)

        Assertions.assertSame(scheduler.async(), scheduler.async())
    }

    @Test
    fun `cancel all cancels registered tasks`() {
        val handle = mockk<ScheduledTask>(relaxed = true)
        every { global.runAtFixedRate(any(), any(), any(), any()) } returns handle
        val scheduler = EcoSchedulerFolia(plugin)
        scheduler.global().runTimer(Runnable { }, 1L, 1L)

        scheduler.cancelAll()

        verify { handle.cancel() }
    }

    @Test
    fun `cancel all also asks folia to cancel plugin tasks`() {
        val scheduler = EcoSchedulerFolia(plugin)

        scheduler.cancelAll()

        verify { global.cancelTasks(plugin) }
        verify { async.cancelTasks(plugin) }
    }

    @Test
    fun `legacy run routes to the global region`() {
        EcoSchedulerFolia(plugin).run { }

        verify { global.run(plugin, any()) }
    }
}
