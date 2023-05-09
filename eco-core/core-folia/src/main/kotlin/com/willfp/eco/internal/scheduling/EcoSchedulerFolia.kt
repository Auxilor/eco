package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit

class EcoSchedulerFolia(private val plugin: EcoPlugin) : Scheduler {
    override fun runLater(runnable: Runnable, ticksLater: Long): BukkitTask {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { runnable.run() }, ticksLater)
    }

    override fun runLater(location: Location, ticksLater: Int, task: Runnable): BukkitTask {
        Bukkit.getRegionScheduler().runDelayed(plugin, location, { task.run() }, ticksLater.toLong())
    }

    override fun runTimer(delay: Long, repeat: Long, runnable: Runnable): BukkitTask {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { runnable.run() }, delay, repeat)
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): BukkitTask {
        Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, { task.run() }, delay.toLong(), repeat.toLong())
    }

    override fun run(runnable: Runnable): BukkitTask {
        Bukkit.getGlobalRegionScheduler().run(plugin) { runnable.run() }
    }

    override fun run(location: Location, task: Runnable): BukkitTask {
        Bukkit.getRegionScheduler().run(plugin, location) { task.run() }
    }

    override fun runAsync(task: Runnable): BukkitTask {
        Bukkit.getAsyncScheduler().runNow(plugin) { task.run() }
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): BukkitTask {
        Bukkit.getAsyncScheduler()
            .runAtFixedRate(plugin, { task.run() }, delay * 50L, repeat * 50L, TimeUnit.MILLISECONDS)
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
        Bukkit.getAsyncScheduler().cancelTasks(plugin)
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin)
    }
}
