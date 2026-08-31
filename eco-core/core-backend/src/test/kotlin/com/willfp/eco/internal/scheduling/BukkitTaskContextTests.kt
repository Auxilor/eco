package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class BukkitTaskContextTests {
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
        every { scheduler.runTaskLaterAsynchronously(any(), any<Runnable>(), any()) } returns mockk<BukkitTask>(relaxed = true)
        every { scheduler.runTaskTimerAsynchronously(any(), any<Runnable>(), any(), any()) } returns mockk<BukkitTask>(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    @Test
    fun `run uses runTask`() {
        BukkitTaskContext(plugin).run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }

    @Test
    fun `run later uses runTaskLater with the same delay`() {
        BukkitTaskContext(plugin).runLater(runnable, 20L)

        verify { scheduler.runTaskLater(plugin, runnable, 20L) }
    }

    @Test
    fun `run later accepts the delay first`() {
        BukkitTaskContext(plugin).runLater(20L, runnable)

        verify { scheduler.runTaskLater(plugin, runnable, 20L) }
    }

    @Test
    fun `run timer uses runTaskTimer with the same delay and period`() {
        BukkitTaskContext(plugin).runTimer(runnable, 5L, 20L)

        verify { scheduler.runTaskTimer(plugin, runnable, 5L, 20L) }
    }

    @Test
    fun `run timer accepts the delay and period first`() {
        BukkitTaskContext(plugin).runTimer(5L, 20L, runnable)

        verify { scheduler.runTaskTimer(plugin, runnable, 5L, 20L) }
    }

    @Test
    fun `delay of zero is passed through unchanged`() {
        BukkitTaskContext(plugin).runLater(runnable, 0L)

        verify { scheduler.runTaskLater(plugin, runnable, 0L) }
    }

    @Test
    fun `run timer hands the task its own handle`() {
        val slot = mutableListOf<Runnable>()
        every { scheduler.runTaskTimer(any(), capture(slot), any(), any()) } returns
                mockk<BukkitTask>(relaxed = true)

        var seen: EcoTask? = null
        val task = BukkitTaskContext(plugin).runTimer(Consumer<EcoTask> { seen = it }, 0L, 20L)
        slot.first().run()

        Assertions.assertSame(task, seen)
    }

    @Test
    fun `async run uses runTaskAsynchronously`() {
        BukkitAsyncTaskContext(plugin).run(runnable)

        verify { scheduler.runTaskAsynchronously(plugin, runnable) }
    }

    @Test
    fun `async run later uses runTaskLaterAsynchronously`() {
        BukkitAsyncTaskContext(plugin).runLater(runnable, 20L)

        verify { scheduler.runTaskLaterAsynchronously(plugin, runnable, 20L) }
    }

    @Test
    fun `async run timer uses runTaskTimerAsynchronously`() {
        BukkitAsyncTaskContext(plugin).runTimer(runnable, 5L, 20L)

        verify { scheduler.runTaskTimerAsynchronously(plugin, runnable, 5L, 20L) }
    }

    @Test
    fun `async time units convert to ticks`() {
        BukkitAsyncTaskContext(plugin).runLater(runnable, 1L, TimeUnit.SECONDS)

        verify { scheduler.runTaskLaterAsynchronously(plugin, runnable, 20L) }
    }

    @Test
    fun `entity context delegates to the sync context and ignores retirement`() {
        val context = BukkitEntityTaskContext(BukkitTaskContext(plugin))

        context.onRetired { }.run(runnable)

        verify { scheduler.runTask(plugin, runnable) }
    }
}
