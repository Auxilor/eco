package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.RunnableTask
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

abstract class EcoRunnableTask(protected val plugin: EcoPlugin) : BukkitRunnable(), RunnableTask {
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
}