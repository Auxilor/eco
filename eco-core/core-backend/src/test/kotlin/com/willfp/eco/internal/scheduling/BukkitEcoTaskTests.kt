package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class BukkitEcoTaskTests {
    private fun plugin(): EcoPlugin = mockk(relaxed = true)

    @Test
    fun `exposes the wrapped bukkit task`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false)
        task.bind(bukkitTask)

        Assertions.assertSame(bukkitTask, task.asBukkitTask())
    }

    @Test
    fun `cancel delegates to the wrapped task`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false)
        task.bind(bukkitTask)

        task.cancel()

        verify { bukkitTask.cancel() }
    }

    @Test
    fun `cancel before bind cancels on bind`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false)

        task.cancel()
        task.bind(bukkitTask)

        Assertions.assertTrue(task.isCancelled)
        verify { bukkitTask.cancel() }
    }

    @Test
    fun `is cancelled reflects the wrapped task once bound`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        every { bukkitTask.isCancelled } returns true
        val task = BukkitEcoTask(plugin(), false)
        task.bind(bukkitTask)

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `repeating flag is whatever it was constructed with`() {
        Assertions.assertTrue(BukkitEcoTask(plugin(), true).isRepeating)
        Assertions.assertFalse(BukkitEcoTask(plugin(), false).isRepeating)
    }
}
