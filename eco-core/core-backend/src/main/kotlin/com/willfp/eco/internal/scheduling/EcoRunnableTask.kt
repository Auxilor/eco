package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.RunnableTask

/**
 * A [RunnableTask] that submits itself through the plugin's scheduler.
 *
 * No longer a `BukkitRunnable`: that type's submit methods throw on Folia, and there is
 * nothing left that needs it now that [EcoTask] carries cancellation.
 *
 * `BukkitRunnable` refused to schedule the same instance twice, throwing
 * `IllegalStateException`, and synchronised every submit method. Both are reproduced here
 * so that behaviour on Paper and Spigot is unchanged: a task is submittable exactly once,
 * including after it has been cancelled, which is what `BukkitRunnable` did.
 */
@Suppress("DEPRECATION")
abstract class EcoRunnableTask(
    protected val plugin: EcoPlugin
) : RunnableTask {
    private var scheduled: EcoTask? = null

    @Synchronized
    private fun submit(submitter: () -> EcoTask): EcoTask {
        check(scheduled == null) { "Already scheduled" }

        val task = submitter()
        scheduled = task
        return task
    }

    override fun runTask(): EcoTask =
        submit { plugin.scheduler.global().run(this) }

    override fun runTaskAsynchronously(): EcoTask =
        submit { plugin.scheduler.async().run(this) }

    override fun runTaskLater(delay: Long): EcoTask =
        submit { plugin.scheduler.global().runLater(this, delay) }

    override fun runTaskLaterAsynchronously(delay: Long): EcoTask =
        submit { plugin.scheduler.async().runLater(this, delay) }

    override fun runTaskTimer(delay: Long, period: Long): EcoTask =
        submit { plugin.scheduler.global().runTimer(this, delay, period) }

    override fun runTaskTimerAsynchronously(delay: Long, period: Long): EcoTask =
        submit { plugin.scheduler.async().runTimer(this, delay, period) }

    /**
     * Cancel the submitted task.
     *
     * `BukkitRunnable.cancel` threw `IllegalStateException` when the runnable had never
     * been scheduled. This is a no-op instead: the only reason this method still exists is
     * for jars compiled against eco 6, and throwing at them from a compatibility shim is
     * worse than doing nothing. Cancelling a task that has been submitted behaves exactly
     * as it did.
     */
    @Synchronized
    override fun cancel() {
        scheduled?.cancel()
    }
}
