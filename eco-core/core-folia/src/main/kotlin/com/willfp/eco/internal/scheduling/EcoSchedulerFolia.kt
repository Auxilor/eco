package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

class EcoSchedulerFolia(private val plugin: EcoPlugin) : Scheduler {
    override fun runLater(runnable: Runnable, ticksLater: Long): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { runnable.run() }, ticksLater))
    }

    override fun runLater(
        location: Location,
        ticksLater: Int,
        task: Runnable
    ): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getRegionScheduler().runDelayed(plugin, location, { task.run() }, ticksLater.toLong()))
    }

    override fun runTimer(delay: Long, repeat: Long, runnable: Runnable): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { runnable.run() }, delay, repeat))
    }

    override fun runTimer(
        location: Location,
        delay: Int,
        repeat: Int,
        task: Runnable
    ): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, { task.run() }, delay.toLong(), repeat.toLong()))
    }

    override fun run(runnable: Runnable): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getGlobalRegionScheduler().run(plugin) { runnable.run() })
    }

    override fun run(location: Location, task: Runnable): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getRegionScheduler().run(plugin, location) { task.run() })
    }

    override fun runAsync(task: Runnable): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getAsyncScheduler().runNow(plugin) { task.run() })
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): com.willfp.eco.core.scheduling.ScheduledTask {
        return TaskAdapter(Bukkit.getAsyncScheduler()
            .runAtFixedRate(plugin, { task.run() }, delay * 50L, repeat * 50L, TimeUnit.MILLISECONDS)
        )
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
        Bukkit.getAsyncScheduler().cancelTasks(plugin)
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin)
    }

    class TaskAdapter(
        private val task: ScheduledTask
    ) : com.willfp.eco.core.scheduling.ScheduledTask {
        override fun getOwningPlugin(): Plugin = task.owningPlugin

        override fun isRepeatingTask(): Boolean = task.isRepeatingTask

        override fun cancel(): com.willfp.eco.core.scheduling.ScheduledTask.CancelledState = when (task.cancel()) {
            ScheduledTask.CancelledState.CANCELLED_BY_CALLER -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.CANCELLED_BY_CALLER
            ScheduledTask.CancelledState.CANCELLED_ALREADY -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.CANCELLED_ALREADY
            ScheduledTask.CancelledState.RUNNING -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.RUNNING
            ScheduledTask.CancelledState.ALREADY_EXECUTED -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.ALREADY_EXECUTED
            ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED
            ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED_ALREADY -> com.willfp.eco.core.scheduling.ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED_ALREADY
        }

        override fun getExecutionState(): com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState =
            when (task.executionState) {
                ScheduledTask.ExecutionState.IDLE -> com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState.IDLE
                ScheduledTask.ExecutionState.RUNNING -> com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState.RUNNING
                ScheduledTask.ExecutionState.FINISHED -> com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState.FINISHED
                ScheduledTask.ExecutionState.CANCELLED -> com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState.CANCELLED
                ScheduledTask.ExecutionState.CANCELLED_RUNNING -> com.willfp.eco.core.scheduling.ScheduledTask.ExecutionState.CANCELLED_RUNNING
            }

    }
}
