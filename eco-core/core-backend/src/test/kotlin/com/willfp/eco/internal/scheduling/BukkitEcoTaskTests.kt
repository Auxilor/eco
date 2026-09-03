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
    fun `is itself the bukkit task`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false, true)
        task.bind(bukkitTask)

        // EcoTask extends BukkitTask for binary compatibility, so this is the task itself
        // rather than the handle it wraps.
        Assertions.assertSame(task, task.asBukkitTask())
    }

    @Test
    fun `reports the wrapped task id`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        every { bukkitTask.taskId } returns 7
        val task = BukkitEcoTask(plugin(), false, true)
        task.bind(bukkitTask)

        Assertions.assertEquals(7, task.taskId)
    }

    @Test
    fun `reports no task id before bind`() {
        Assertions.assertEquals(-1, BukkitEcoTask(plugin(), false, true).taskId)
    }

    @Test
    fun `reports whether it is sync`() {
        Assertions.assertTrue(BukkitEcoTask(plugin(), false, true).isSync)
        Assertions.assertFalse(BukkitEcoTask(plugin(), false, false).isSync)
    }

    @Test
    fun `cancel delegates to the wrapped task`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false, true)
        task.bind(bukkitTask)

        task.cancel()

        verify { bukkitTask.cancel() }
    }

    @Test
    fun `cancel before bind cancels on bind`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        val task = BukkitEcoTask(plugin(), false, true)

        task.cancel()
        task.bind(bukkitTask)

        Assertions.assertTrue(task.isCancelled)
        verify { bukkitTask.cancel() }
    }

    @Test
    fun `is cancelled reflects the wrapped task once bound`() {
        val bukkitTask = mockk<BukkitTask>(relaxed = true)
        every { bukkitTask.isCancelled } returns true
        val task = BukkitEcoTask(plugin(), false, true)
        task.bind(bukkitTask)

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `repeating flag is whatever it was constructed with`() {
        Assertions.assertTrue(BukkitEcoTask(plugin(), true, true).isRepeating)
        Assertions.assertFalse(BukkitEcoTask(plugin(), false, true).isRepeating)
    }
}
