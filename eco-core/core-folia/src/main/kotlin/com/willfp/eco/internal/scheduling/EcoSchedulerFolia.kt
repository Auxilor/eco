package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit

class EcoSchedulerFolia(private val plugin: EcoPlugin) : Scheduler {

     private lateinit var task : ScheduledTask

    override fun runLater(runnable: Runnable, ticksLater: Long): BukkitTask? {
        this.task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { runnable.run() }, ticksLater)
        return null
    }

    override fun runLater(location: Location, ticksLater: Int, task: Runnable): BukkitTask? {
        this.task = Bukkit.getRegionScheduler().runDelayed(plugin, location, { task.run() }, ticksLater.toLong())
        return null
    }

    override fun runTimer(delay: Long, repeat: Long, runnable: Runnable): BukkitTask? {
        this.task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { runnable.run() }, delay, repeat)
        return null
    }

    override fun runTimer(location: Location, delay: Int, repeat: Int, task: Runnable): BukkitTask? {
        this.task = Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, { task.run() }, delay.toLong(), repeat.toLong())
        return null
    }

    override fun run(runnable: Runnable): BukkitTask? {
        this.task = Bukkit.getGlobalRegionScheduler().run(plugin) { runnable.run() }
        return null
    }

    override fun run(location: Location, task: Runnable): BukkitTask? {
        this.task = Bukkit.getRegionScheduler().run(plugin, location) { task.run() }
        return null
    }

    override fun runAsync(task: Runnable): BukkitTask? {
        this.task = Bukkit.getAsyncScheduler().runNow(plugin) { task.run() }
        return null
    }

    override fun runTimerAsync(delay: Int, repeat: Int, task: Runnable): BukkitTask? {
        this.task = Bukkit.getAsyncScheduler()
            .runAtFixedRate(plugin, { task.run() }, delay * 50L, repeat * 50L, TimeUnit.MILLISECONDS)
        return null
    }

    override fun cancelAll() {
        Bukkit.getAsyncScheduler().cancelTasks(plugin)
        Bukkit.getGlobalRegionScheduler().cancelTasks(plugin)
    }

    override fun cancel() {
        if (!this.task.isCancelled) this.task.cancel()
    }
}
