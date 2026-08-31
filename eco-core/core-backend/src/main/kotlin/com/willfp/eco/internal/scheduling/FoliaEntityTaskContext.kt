package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.EntityTaskContext
import java.util.function.Consumer
import org.bukkit.entity.Entity

/**
 * Tasks on the Folia region owning an entity, following it as it moves.
 *
 * Folia returns null instead of a task when the entity is already gone, and does not run
 * the retirement action in that case. Callers should not have to tell those two flavours
 * of retirement apart, so this fires the action itself and hands back a cancelled task.
 *
 * Loaded only on Folia.
 */
class FoliaEntityTaskContext(
    private val plugin: EcoPlugin,
    private val registry: MutableSet<FoliaEcoTask>,
    private val entity: Entity,
    private val retired: Runnable = Runnable { }
) : EntityTaskContext {
    override fun onRetired(onRetired: Runnable): EntityTaskContext =
        FoliaEntityTaskContext(plugin, registry, entity, onRetired)

    private fun submit(
        repeating: Boolean,
        submitter: (FoliaEcoTask) -> io.papermc.paper.threadedregions.scheduler.ScheduledTask?
    ): EcoTask {
        val task = FoliaEcoTask(plugin, repeating, registry)
        registry.add(task)

        val handle = submitter(task)

        if (handle == null) {
            retired.run()
        }

        task.bind(handle)

        return task
    }

    override fun run(runnable: Runnable): EcoTask = submit(false) { task ->
        entity.scheduler.run(plugin, task.wrap(runnable), retired)
    }

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask {
        if (ticksLater <= 0) {
            return run(runnable)
        }

        return submit(false) { task ->
            entity.scheduler.runDelayed(plugin, task.wrap(runnable), retired, ticksLater)
        }
    }

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask =
        submit(true) { task ->
            entity.scheduler.runAtFixedRate(
                plugin,
                task.wrap(runnable),
                retired,
                delay.coerceAtLeast(1),
                repeat.coerceAtLeast(1)
            )
        }

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask =
        submit(true) { task ->
            entity.scheduler.runAtFixedRate(
                plugin,
                task.wrapSelf(runnable),
                retired,
                delay.coerceAtLeast(1),
                repeat.coerceAtLeast(1)
            )
        }
}
