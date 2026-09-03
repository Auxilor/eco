package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.EntityTaskContext
import com.willfp.eco.core.scheduling.TaskContext
import java.util.function.Consumer

/**
 * Entity affinity means nothing on Paper and Spigot, so this delegates everything to the
 * sync context and discards the retirement action.
 */
class BukkitEntityTaskContext(
    private val delegate: TaskContext
) : EntityTaskContext {
    override fun onRetired(onRetired: Runnable): EntityTaskContext = this

    override fun run(runnable: Runnable): EcoTask = delegate.run(runnable)

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask =
        delegate.runLater(runnable, ticksLater)

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask =
        delegate.runTimer(runnable, delay, repeat)

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask =
        delegate.runTimer(runnable, delay, repeat)
}
