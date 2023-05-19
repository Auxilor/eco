package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.RunnableTask
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

abstract class EcoRunnableTask(protected val plugin: EcoPlugin) : BukkitRunnable(), RunnableTask, Scheduler {
    @Synchronized
    override fun runTask(): BukkitTask {
        return super.runTask(plugin)
    }

    @Synchronized
    override fun runTaskAsynchronously(): BukkitTask {
        return super.runTaskAsynchronously(plugin)
    }

    @Synchronized
    override fun runTaskLater(delay: Long): BukkitTask {
        return super.runTaskLater(plugin, delay)
    }

    @Synchronized
    override fun runTaskLaterAsynchronously(delay: Long): BukkitTask {
        return super.runTaskLaterAsynchronously(plugin, delay)
    }

    @Synchronized
    override fun runTaskTimer(delay: Long, period: Long): BukkitTask {
        return super.runTaskTimer(plugin, delay, period)
    }

    @Synchronized
    override fun runTaskTimerAsynchronously(delay: Long, period: Long): BukkitTask {
        return super.runTaskTimerAsynchronously(plugin, delay, period)
    }

    override fun cancelAll() {
        plugin.scheduler.cancelAll()
    }

    override fun runAsync(task: Runnable): Scheduler {
        return plugin.scheduler.runAsync(task)
    }

    override fun run(location: Location, task: Runnable): Scheduler {
        return plugin.scheduler.run(location, task)
    }

    override fun runLater(location: Location, ticksLater: Int, task: Runnable): Scheduler {
        return plugin.scheduler.runLater(location, ticksLater, task)
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): Scheduler {
        return plugin.scheduler.runTimer(location, delay, repeat, task)
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): Scheduler {
        return plugin.scheduler.runTimerAsync(delay, repeat, task)
    }
}