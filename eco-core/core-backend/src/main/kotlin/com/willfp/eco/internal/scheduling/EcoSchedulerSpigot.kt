package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask

class EcoSchedulerSpigot(private val plugin: EcoPlugin) : Scheduler {

    private lateinit var task : BukkitTask

    override fun runLater(location: Location, ticksLater: Int, task: Runnable): BukkitTask {
        this.task = Bukkit.getScheduler().runTaskLater(plugin, task, ticksLater.toLong())
        return this.task
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): BukkitTask {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, task, delay.toLong(), repeat.toLong())
        return this.task
    }

    override fun run(location: Location, task: Runnable): BukkitTask {
        this.task = Bukkit.getScheduler().runTask(plugin, task)
        return this.task
    }

    override fun runAsync(task: Runnable): BukkitTask {
        this.task = Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
        return this.task
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): BukkitTask {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay.toLong(), repeat.toLong())
        return this.task
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }

    override fun cancel() {
        if (!this.task.isCancelled) this.task.cancel()
    }
}