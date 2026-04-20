package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoWrappedTask
import com.willfp.eco.core.scheduling.RunnableTask
import kotlinx.coroutines.Runnable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity

abstract class EcoRunnableTaskSpigot(protected val plugin: EcoPlugin) : RunnableTask {
    private var task: EcoWrappedTaskSpigot? = null

    @Synchronized
    override fun runTask(): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(Bukkit.getScheduler().runTask(plugin, runnable))
        return task!!
    }

    @Synchronized
    override fun runTaskAsynchronously(): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable))
        return task!!
    }

    @Synchronized
    override fun runTaskLater(delay: Long): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay))
        return task!!
    }

    @Synchronized
    override fun runTaskLaterAsynchronously(delay: Long): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay))
        return task!!
    }

    @Synchronized
    override fun runTaskTimer(delay: Long, period: Long): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period), true)
        return task!!
    }

    @Synchronized
    override fun runTaskTimerAsynchronously(delay: Long, period: Long): EcoWrappedTaskSpigot {
        val runnable: Runnable = { this.run() }
        task = EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period),
            true
        )
        return task!!
    }

    @Synchronized
    override fun runTask(location: Location): EcoWrappedTaskSpigot {
        return runTask()
    }

    @Synchronized
    override fun runTaskLater(location: Location, delay: Long): EcoWrappedTaskSpigot {
        return runTaskLater(delay)
    }

    @Synchronized
    override fun runTaskTimer(location: Location, delay: Long, period: Long): EcoWrappedTaskSpigot {
        return runTaskTimer(delay, period)
    }

    @Synchronized
    override fun runTask(entity: Entity): EcoWrappedTaskSpigot {
        return runTask()
    }

    @Synchronized
    override fun runTaskLater(entity: Entity, delay: Long): EcoWrappedTaskSpigot {
        return runTaskLater(delay)
    }

    @Synchronized
    override fun runTaskTimer(entity: Entity, delay: Long, period: Long): EcoWrappedTaskSpigot {
        return runTaskTimer(delay, period)
    }

    @Synchronized
    override fun cancelTask(): EcoWrappedTask.CancelledState? {
        return task?.cancelTask()
    }
}