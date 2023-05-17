package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask

class EcoSchedulerSpigot(private val plugin: EcoPlugin) : Scheduler {

    private lateinit var task: BukkitTask

    override fun runLater(location: Location, ticksLater: Int, task: Runnable): Scheduler {
        this.task = Bukkit.getScheduler().runTaskLater(plugin, task, ticksLater.toLong())
        return this
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): Scheduler {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, task, delay.toLong(), repeat.toLong())
        return this
    }

    override fun run(location: Location, task: Runnable): Scheduler {
        this.task = Bukkit.getScheduler().runTask(plugin, task)
        return this
    }

    override fun getTaskId(): Int {
        return this.task.taskId
    }

    override fun runAsync(task: Runnable): Scheduler {
        this.task = Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
        return this
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): Scheduler {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay.toLong(), repeat.toLong())
        return this
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }

    override fun cancel() {
        if (!this.task.isCancelled) this.task.cancel()
    }
}