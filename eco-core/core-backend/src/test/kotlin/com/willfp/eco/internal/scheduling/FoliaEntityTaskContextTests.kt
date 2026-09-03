package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import io.papermc.paper.threadedregions.scheduler.EntityScheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.entity.Entity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FoliaEntityTaskContextTests {
    private lateinit var plugin: EcoPlugin
    private lateinit var entity: Entity
    private lateinit var entityScheduler: EntityScheduler
    private val registry = ConcurrentHashMap.newKeySet<FoliaEcoTask>()
    private val runnable = Runnable { }

    @BeforeEach
    fun setUp() {
        plugin = mockk(relaxed = true)
        entityScheduler = mockk(relaxed = true)
        entity = mockk(relaxed = true)
        registry.clear()
        every { entity.scheduler } returns entityScheduler
        every { entityScheduler.run(any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { entityScheduler.runDelayed(any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
        every { entityScheduler.runAtFixedRate(any(), any(), any(), any(), any()) } returns mockk<ScheduledTask>(relaxed = true)
    }

    private fun context() = FoliaEntityTaskContext(plugin, registry, entity)

    @Test
    fun `run uses the entity scheduler`() {
        context().run(runnable)

        verify { entityScheduler.run(plugin, any(), any()) }
    }

    @Test
    fun `run later uses a delayed task`() {
        context().runLater(runnable, 20L)

        verify { entityScheduler.runDelayed(plugin, any(), any(), 20L) }
    }

    @Test
    fun `a delay of zero becomes a next tick task`() {
        context().runLater(runnable, 0L)

        verify { entityScheduler.run(plugin, any(), any()) }
    }

    @Test
    fun `run timer clamps the delay and period`() {
        context().runTimer(runnable, 0L, 0L)

        verify { entityScheduler.runAtFixedRate(plugin, any(), any(), 1L, 1L) }
    }

    @Test
    fun `on retired returns a new context`() {
        val context = context()

        Assertions.assertNotSame(context, context.onRetired { })
    }

    @Test
    fun `the retirement action is passed to folia`() {
        val retired = Runnable { }

        context().onRetired(retired).run(runnable)

        verify { entityScheduler.run(plugin, any(), retired) }
    }

    @Test
    fun `a retired entity fires the retirement action`() {
        every { entityScheduler.run(any(), any(), any()) } returns null
        var fired = false

        context().onRetired { fired = true }.run(runnable)

        Assertions.assertTrue(fired)
    }

    @Test
    fun `a retired entity yields a cancelled task`() {
        every { entityScheduler.run(any(), any(), any()) } returns null

        val task = context().run(runnable)

        Assertions.assertTrue(task.isCancelled)
    }

    @Test
    fun `a retired entity leaves nothing in the registry`() {
        every { entityScheduler.run(any(), any(), any()) } returns null

        context().run(runnable)

        Assertions.assertTrue(registry.isEmpty())
    }
}
