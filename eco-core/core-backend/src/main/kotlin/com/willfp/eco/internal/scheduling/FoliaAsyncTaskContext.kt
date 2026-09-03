package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.AsyncTaskContext
import com.willfp.eco.core.scheduling.EcoTask
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import org.bukkit.Bukkit

private const val MILLIS_PER_TICK = 50L

/**
 * Off-thread tasks on Folia.
 *
 * Folia's async scheduler measures time, not ticks, so tick-based delays are converted.
 * That makes them wall-clock rather than tick-driven, which is a real difference from
 * Paper on a lagging server, and the reason the [TimeUnit] overloads exist.
 *
 * Loaded only on Folia.
 */
class FoliaAsyncTaskContext(
    private val plugin: EcoPlugin,
    private val registry: MutableSet<FoliaEcoTask>
) : AsyncTaskContext {
    private fun submit(repeating: Boolean, submitter: (FoliaEcoTask) -> Unit): EcoTask {
        val task = FoliaEcoTask(plugin, repeating, registry, false)
        registry.add(task)
        submitter(task)
        return task
    }

    override fun run(runnable: Runnable): EcoTask = submit(false) { task ->
        task.bind(Bukkit.getAsyncScheduler().runNow(plugin, task.wrap(runnable)))
    }

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask {
        if (ticksLater <= 0) {
            return run(runnable)
        }

        return runLater(runnable, ticksLater * MILLIS_PER_TICK, TimeUnit.MILLISECONDS)
    }

    override fun runLater(runnable: Runnable, delay: Long, unit: TimeUnit): EcoTask =
        submit(false) { task ->
            task.bind(
                Bukkit.getAsyncScheduler().runDelayed(plugin, task.wrap(runnable), delay, unit)
            )
        }

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask =
        runTimer(
            runnable,
            delay.coerceAtLeast(0) * MILLIS_PER_TICK,
            repeat.coerceAtLeast(1) * MILLIS_PER_TICK,
            TimeUnit.MILLISECONDS
        )

    override fun runTimer(
        runnable: Runnable,
        delay: Long,
        period: Long,
        unit: TimeUnit
    ): EcoTask = submit(true) { task ->
        task.bind(
            Bukkit.getAsyncScheduler()
                .runAtFixedRate(plugin, task.wrap(runnable), delay, period, unit)
        )
    }

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask =
        submit(true) { task ->
            task.bind(
                Bukkit.getAsyncScheduler().runAtFixedRate(
                    plugin,
                    task.wrapSelf(runnable),
                    delay.coerceAtLeast(0) * MILLIS_PER_TICK,
                    repeat.coerceAtLeast(1) * MILLIS_PER_TICK,
                    TimeUnit.MILLISECONDS
                )
            )
        }
}
