package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EcoSchedulerBukkitTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var scheduler: BukkitScheduler
    private val runnable = Runnable { }

    @BeforeEach
    fun setUp() {
        plugin = mockk(relaxed = true)
        scheduler = mockk(relaxed = true)
        mockkStatic(Bukkit::class)
        every { Bukkit.getScheduler() } returns scheduler
        every { scheduler.runTask(any(), any<Runnable>()) } returns mockk<BukkitTask>(relaxed = true)
        every { scheduler.runTaskLater(any(), any<Runnable>(), any()) } returns mockk<BukkitTask>(relaxed = true)
        every { scheduler.runTaskTimer(any(), any<Runnable>(), any(), any()) } returns mockk<BukkitTask>(relaxed = true)
        every { scheduler.runTaskAsynchronously(any(), any<Runnable>()) } returns mockk<BukkitTask>(relaxed = true)
        every { scheduler.runTaskTimerAsynchronously(any(), any<Runnable>(), any(), any()) } returns mockk<BukkitTask>(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    @Test
    fun `legacy run matches the global context`() {
        EcoSchedulerBukkit(plugin).run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }

    @Test
    fun `legacy run later matches the global context`() {
        EcoSchedulerBukkit(plugin).runLater(runnable, 20L)

        verify { scheduler.runTaskLater(plugin, runnable, 20L) }
    }

    @Test
    fun `legacy run timer matches the global context`() {
        EcoSchedulerBukkit(plugin).runTimer(runnable, 5L, 20L)

        verify { scheduler.runTaskTimer(plugin, runnable, 5L, 20L) }
    }

    @Test
    fun `legacy run async matches the async context`() {
        EcoSchedulerBukkit(plugin).runAsync(runnable)

        verify { scheduler.runTaskAsynchronously(plugin, runnable) }
    }

    @Test
    fun `legacy run async timer matches the async context`() {
        EcoSchedulerBukkit(plugin).runAsyncTimer(runnable, 5L, 20L)

        verify { scheduler.runTaskTimerAsynchronously(plugin, runnable, 5L, 20L) }
    }

    @Test
    fun `location context runs on the main thread`() {
        val location = mockk<Location>(relaxed = true)

        EcoSchedulerBukkit(plugin).at(location).run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }

    @Test
    fun `chunk context runs on the main thread`() {
        val world = mockk<World>(relaxed = true)

        EcoSchedulerBukkit(plugin).at(world, 0, 0).run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }

    @Test
    fun `entity context runs on the main thread`() {
        val entity = mockk<Entity>(relaxed = true)

        EcoSchedulerBukkit(plugin).on(entity).run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }

    @Test
    fun `cancel all cancels this plugin's bukkit tasks`() {
        EcoSchedulerBukkit(plugin).cancelAll()

        verify { scheduler.cancelTasks(plugin) }
    }
}
