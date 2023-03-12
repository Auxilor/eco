package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask

class EcoSchedulerSpigot(private val plugin: EcoPlugin) : Scheduler {
    override fun runLater(location: Location, ticksLater: Int, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, task, ticksLater.toLong())
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay.toLong(), repeat.toLong())
    }

    override fun run(location: Location, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, task)
    }

    override fun runAsync(task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay.toLong(), repeat.toLong())
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }
}