package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

class EcoScheduler(private val plugin: EcoPlugin) : Scheduler {
    override fun runLater(
        runnable: Runnable,
        ticksLater: Long
    ): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, ticksLater)
    }

    override fun runTimer(
        runnable: Runnable,
        delay: Long,
        repeat: Long
    ): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, repeat)
    }

    override fun runAsyncTimer(
        runnable: Runnable,
        delay: Long,
        repeat: Long
    ): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, repeat)
    }

    override fun run(runnable: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, runnable)
    }

    override fun runAsync(runnable: Runnable): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
    }

    override fun syncRepeating(
        runnable: Runnable,
        delay: Long,
        repeat: Long
    ): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, repeat)
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }
}