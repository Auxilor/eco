package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EcoRunnableTaskTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var scheduler: BukkitScheduler

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
        every { plugin.scheduler } returns EcoSchedulerBukkit(plugin)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(Bukkit::class)
    }

    private fun task(body: () -> Unit = { }) = object : EcoRunnableTask(plugin) {
        override fun run() = body()
    }

    @Test
    fun `run task goes through the global context`() {
        task().runTask()

        verify { scheduler.runTask(plugin, any<Runnable>()) }
    }

    @Test
    fun `run task later goes through the global context`() {
        task().runTaskLater(20L)

        verify { scheduler.runTaskLater(plugin, any<Runnable>(), 20L) }
    }

    @Test
    fun `run task timer goes through the global context`() {
        task().runTaskTimer(5L, 20L)

        verify { scheduler.runTaskTimer(plugin, any<Runnable>(), 5L, 20L) }
    }

    @Test
    fun `run task asynchronously goes through the async context`() {
        task().runTaskAsynchronously()

        verify { scheduler.runTaskAsynchronously(plugin, any<Runnable>()) }
    }

    @Test
    fun `the body is what gets scheduled`() {
        val slot = mutableListOf<Runnable>()
        every { scheduler.runTask(any(), capture(slot)) } returns mockk<BukkitTask>(relaxed = true)
        var ran = false

        task { ran = true }.runTask()
        slot.first().run()

        org.junit.jupiter.api.Assertions.assertTrue(ran)
    }
}
