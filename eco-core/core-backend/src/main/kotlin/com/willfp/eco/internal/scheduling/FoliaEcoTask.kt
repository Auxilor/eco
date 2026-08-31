package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import java.util.function.Consumer
import org.bukkit.scheduler.BukkitTask

/**
 * An [EcoTask] backed by a Folia [ScheduledTask].
 *
 * Folia hands back the [ScheduledTask] only after the task has been submitted, but a
 * repeating task needs its own handle from its first run, so the handle is created first
 * and bound afterwards. Two orderings have to work: the task running before [bind], and
 * [cancel] arriving before [bind].
 *
 * Loaded only on Folia. Never referenced from a class Spigot can load.
 */
class FoliaEcoTask(
    private val plugin: EcoPlugin,
    private val repeating: Boolean,
    private val registry: MutableSet<FoliaEcoTask>
) : EcoTask {
    @Volatile
    private var handle: ScheduledTask? = null

    @Volatile
    private var cancelRequested = false

    /**
     * Bind the submitted task. A null task means Folia refused to schedule it, which
     * happens when the target entity has already been removed.
     */
    fun bind(task: ScheduledTask?) {
        if (task == null) {
            cancelRequested = true
            registry.remove(this)
            return
        }

        handle = task

        if (cancelRequested) {
            task.cancel()
            registry.remove(this)
        }
    }

    /**
     * Wrap a task so that it deregisters itself once it can no longer run.
     */
    fun wrap(runnable: Runnable): Consumer<ScheduledTask> = Consumer {
        try {
            runnable.run()
        } finally {
            if (!repeating) {
                registry.remove(this)
            }
        }
    }

    /**
     * Wrap a task that wants its own handle, so it can cancel itself.
     */
    fun wrapSelf(consumer: Consumer<EcoTask>): Consumer<ScheduledTask> = Consumer {
        try {
            consumer.accept(this)
        } finally {
            if (!repeating) {
                registry.remove(this)
            }
        }
    }

    override fun cancel() {
        cancelRequested = true
        handle?.cancel()
        registry.remove(this)
    }

    override fun isCancelled(): Boolean = cancelRequested || (handle?.isCancelled ?: false)

    override fun isRepeating(): Boolean = repeating

    override fun getPlugin(): EcoPlugin = plugin

    override fun asBukkitTask(): BukkitTask? = null
}
