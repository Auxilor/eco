package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.ConcurrentHashMap
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FoliaEcoTaskTests {
    private fun plugin(): EcoPlugin = mockk(relaxed = true)

    private fun registry(): MutableSet<FoliaEcoTask> = ConcurrentHashMap.newKeySet()

    @Test
    fun `cancel delegates to the bound task`() {
        val handle = mockk<ScheduledTask>(relaxed = true)
        val task = FoliaEcoTask(plugin(), false, registry(), true)
        task.bind(handle)

        task.cancel()

        verify { handle.cancel() }
    }

    @Test
    fun `cancel before bind is applied on bind`() {
        val handle = mockk<ScheduledTask>(relaxed = true)
        val task = FoliaEcoTask(plugin(), false, registry(), true)

        task.cancel()
        task.bind(handle)

        verify { handle.cancel() }
    }

    @Test
    fun `cancel before bind reports cancelled`() {
        val task = FoliaEcoTask(plugin(), false, registry(), true)

        task.cancel()

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `binding null marks the task cancelled`() {
        val task = FoliaEcoTask(plugin(), false, registry(), true)

        task.bind(null)

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `a one shot leaves the registry once it has run`() {
        val registry = registry()
        val task = FoliaEcoTask(plugin(), false, registry, true)
        registry.add(task)

        task.wrap { }.accept(mockk(relaxed = true))

        Assertions.assertTrue(registry.isEmpty())
    }

    @Test
    fun `a one shot leaves the registry even if it throws`() {
        val registry = registry()
        val task = FoliaEcoTask(plugin(), false, registry, true)
        registry.add(task)

        Assertions.assertThrows(IllegalStateException::class.java) {
            task.wrap { throw IllegalStateException("boom") }.accept(mockk(relaxed = true))
        }
        Assertions.assertTrue(registry.isEmpty())
    }

    @Test
    fun `a repeating task stays in the registry after running`() {
        val registry = registry()
        val task = FoliaEcoTask(plugin(), true, registry, true)
        registry.add(task)

        task.wrap { }.accept(mockk(relaxed = true))

        Assertions.assertTrue(registry.contains(task))
    }

    @Test
    fun `cancel removes the task from the registry`() {
        val registry = registry()
        val task = FoliaEcoTask(plugin(), true, registry, true)
        registry.add(task)

        task.cancel()

        Assertions.assertTrue(registry.isEmpty())
    }

    @Test
    fun `running before bind does not fail`() {
        val registry = registry()
        val task = FoliaEcoTask(plugin(), false, registry, true)
        registry.add(task)

        Assertions.assertDoesNotThrow {
            task.wrap { }.accept(mockk(relaxed = true))
        }
    }

    @Test
    fun `wrap self hands the task its own handle`() {
        val task = FoliaEcoTask(plugin(), true, registry(), true)
        var seen: Any? = null

        task.wrapSelf { seen = it }.accept(mockk(relaxed = true))

        Assertions.assertSame(task, seen)
    }

    @Test
    fun `is cancelled reflects the bound task`() {
        val handle = mockk<ScheduledTask>(relaxed = true)
        every { handle.isCancelled } returns true
        val task = FoliaEcoTask(plugin(), false, registry(), true)
        task.bind(handle)

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `is itself the bukkit task`() {
        val task = FoliaEcoTask(plugin(), false, registry(), true)

        Assertions.assertSame(task, task.asBukkitTask())
    }

    @Test
    fun `reports no task id`() {
        // Folia has no task ID space, so -1 is the only honest answer.
        Assertions.assertEquals(-1, FoliaEcoTask(plugin(), false, registry(), true).taskId)
    }

    @Test
    fun `reports whether it is sync`() {
        Assertions.assertTrue(FoliaEcoTask(plugin(), false, registry(), true).isSync)
        Assertions.assertFalse(FoliaEcoTask(plugin(), false, registry(), false).isSync)
    }
}
