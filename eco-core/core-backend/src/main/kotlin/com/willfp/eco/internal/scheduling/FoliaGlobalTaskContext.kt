package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.TaskContext
import java.util.function.Consumer
import org.bukkit.Bukkit

/**
 * Tasks on Folia's global region: plugin state, configs, databases.
 *
 * Folia rejects a delay or period below one, while Bukkit accepts zero, so a delay of
 * zero or less collapses to a next-tick task, which is what Bukkit does with it anyway,
 * and a period below one is raised to one, which is also what Bukkit does with it.
 *
 * Loaded only on Folia.
 */
class FoliaGlobalTaskContext(
    private val plugin: EcoPlugin,
    private val registry: MutableSet<FoliaEcoTask>
) : TaskContext {
    private fun submit(repeating: Boolean, submitter: (FoliaEcoTask) -> Unit): EcoTask {
        val task = FoliaEcoTask(plugin, repeating, registry)
        registry.add(task)
        submitter(task)
        return task
    }

    override fun run(runnable: Runnable): EcoTask = submit(false) { task ->
        task.bind(Bukkit.getGlobalRegionScheduler().run(plugin, task.wrap(runnable)))
    }

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask {
        if (ticksLater <= 0) {
            return run(runnable)
        }

        return submit(false) { task ->
            task.bind(
                Bukkit.getGlobalRegionScheduler()
                    .runDelayed(plugin, task.wrap(runnable), ticksLater)
            )
        }
    }

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask =
        submit(true) { task ->
            task.bind(
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    task.wrap(runnable),
                    delay.coerceAtLeast(1),
                    repeat.coerceAtLeast(1)
                )
            )
        }

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask =
        submit(true) { task ->
            task.bind(
                Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    task.wrapSelf(runnable),
                    delay.coerceAtLeast(1),
                    repeat.coerceAtLeast(1)
                )
            )
        }
}
