package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import org.bukkit.Bukkit
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FoliaAsyncTaskContextTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var async: AsyncScheduler
    private val registry = ConcurrentHashMap.newKeySet<FoliaEcoTask>()
    private val runnable = Runnable { }

    @BeforeEach
    fun setUp() {
        plugin = mockk(relaxed = true)
        async = mockk(relaxed = true)
        registry.clear()
        mockkStatic(Bukkit::class)
        every { Bukkit.getAsyncScheduler() } returns async
        every { async.runNow(any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { async.runDelayed(any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { async.runAtFixedRate(any(), any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    private fun context() = FoliaAsyncTaskContext(plugin, registry)

    @Test
    fun `run uses run now`() {
        context().run(runnable)

        verify { async.runNow(plugin, any()) }
    }

    @Test
    fun `ticks become milliseconds`() {
        context().runLater(runnable, 20L)

        verify { async.runDelayed(plugin, any(), 1000L, TimeUnit.MILLISECONDS) }
    }

    @Test
    fun `a delay of zero runs now`() {
        context().runLater(runnable, 0L)

        verify { async.runNow(plugin, any()) }
    }

    @Test
    fun `timer ticks become milliseconds`() {
        context().runTimer(runnable, 5L, 20L)

        verify { async.runAtFixedRate(plugin, any(), 250L, 1000L, TimeUnit.MILLISECONDS) }
    }

    @Test
    fun `a timer period below one tick becomes one tick`() {
        context().runTimer(runnable, 5L, 0L)

        verify { async.runAtFixedRate(plugin, any(), 250L, 50L, TimeUnit.MILLISECONDS) }
    }

    @Test
    fun `time units are passed straight through`() {
        context().runLater(runnable, 3L, TimeUnit.SECONDS)

        verify { async.runDelayed(plugin, any(), 3L, TimeUnit.SECONDS) }
    }

    @Test
    fun `timer time units are passed straight through`() {
        context().runTimer(runnable, 1L, 5L, TimeUnit.MINUTES)

        verify { async.runAtFixedRate(plugin, any(), 1L, 5L, TimeUnit.MINUTES) }
    }
}
