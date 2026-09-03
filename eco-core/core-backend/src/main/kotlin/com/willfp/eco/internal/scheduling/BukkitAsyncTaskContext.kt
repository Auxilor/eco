package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.EcoTask
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import org.bukkit.Bukkit

private const val MILLIS_PER_TICK = 50L

/**
 * Off-thread tasks on Paper and Spigot. Time units are converted to ticks, because the
 * Bukkit scheduler only counts ticks.
 */
class BukkitAsyncTaskContext(
    private val plugin: EcoPlugin
) : AsyncTaskContext {
    override fun run(runnable: Runnable): EcoTask {
        val task = BukkitEcoTask(plugin, false, false)
        task.bind(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable))
        return task
    }

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask {
        val task = BukkitEcoTask(plugin, false, false)
        task.bind(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticksLater))
        return task
    }

    override fun runLater(runnable: Runnable, delay: Long, unit: TimeUnit): EcoTask =
        runLater(runnable, unit.toMillis(delay) / MILLIS_PER_TICK)

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask {
        val task = BukkitEcoTask(plugin, true, false)
        task.bind(
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, repeat)
        )
        return task
    }

    override fun runTimer(runnable: Runnable, delay: Long, period: Long, unit: TimeUnit): EcoTask =
        runTimer(
            runnable,
            unit.toMillis(delay) / MILLIS_PER_TICK,
            unit.toMillis(period) / MILLIS_PER_TICK
        )

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask {
        val task = BukkitEcoTask(plugin, true, false)
        task.bind(
            Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, Runnable { runnable.accept(task) }, delay, repeat)
        )
        return task
    }
}
