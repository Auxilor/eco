package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.ScheduledTask
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class EcoSchedulerSpigot(private val plugin: EcoPlugin) : Scheduler {
    override fun runLater(location: Location, ticksLater: Int, task: Runnable): ScheduledTask {
        return TaskAdapter(Bukkit.getScheduler().runTaskLater(plugin, task, ticksLater.toLong()))
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): ScheduledTask {
        return TaskAdapter(Bukkit.getScheduler().runTaskTimer(plugin, task, delay.toLong(), repeat.toLong()), true)
    }

    override fun run(location: Location, task: Runnable): ScheduledTask {
        return TaskAdapter(Bukkit.getScheduler().runTask(plugin, task))
    }

    override fun runAsync(task: Runnable): ScheduledTask {
        return TaskAdapter(Bukkit.getScheduler().runTaskAsynchronously(plugin, task))
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): ScheduledTask {
        return TaskAdapter(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay.toLong(), repeat.toLong()), true)
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }

    // Should we monitor the status of the task?
    class TaskAdapter(
        private val task: BukkitTask,
        private val repeatable: Boolean = false
    ) : ScheduledTask {
        override fun getOwningPlugin(): Plugin = task.owner

        override fun isRepeatingTask(): Boolean = repeatable

        override fun cancel(): ScheduledTask.CancelledState = when (task.cancel().run { task.isCancelled }) {
            true -> ScheduledTask.CancelledState.CANCELLED_ALREADY
            false -> ScheduledTask.CancelledState.RUNNING
        }

        override fun getExecutionState(): ScheduledTask.ExecutionState = when (task.isCancelled) {
            true -> ScheduledTask.ExecutionState.CANCELLED
            false -> ScheduledTask.ExecutionState.RUNNING
        }

    }
}