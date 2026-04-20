package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.Scheduler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.concurrent.FutureTask

class EcoSchedulerSpigot(private val plugin: EcoPlugin) : Scheduler {
    override fun runTaskLater(
        task: FutureTask<*>,
        ticksLater: Long
    ): EcoWrappedTaskSpigot {
        return EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTaskLater(plugin, task, ticksLater)
        )
    }

    override fun runTaskLater(
        task: FutureTask<*>,
        location: Location,
        ticksLater: Long
    ): EcoWrappedTaskSpigot {
        return runTaskLater(task, ticksLater)
    }

    override fun runTaskLater(
        task: FutureTask<*>,
        entity: Entity,
        ticksLater: Long
    ): EcoWrappedTaskSpigot {
        return runTaskLater(task, ticksLater)
    }

    override fun runTaskTimer(
        runnable: Runnable,
        delay: Long,
        repeat: Long
    ): EcoWrappedTaskSpigot {
        return EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, repeat), true
        )
    }

    override fun runTaskTimer(
        runnable: Runnable,
        location: Location,
        delay: Long,
        repeat: Long
    ): EcoWrappedTaskSpigot {
        return runTaskTimer(runnable, delay, repeat)
    }

    override fun runTaskTimer(
        runnable: Runnable,
        entity: Entity,
        delay: Long,
        repeat: Long
    ): EcoWrappedTaskSpigot {
        return runTaskTimer(runnable, delay, repeat)
    }

    override fun runTaskAsyncTimer(
        runnable: Runnable,
        delay: Long,
        repeat: Long
    ): EcoWrappedTaskSpigot {
        return EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, repeat), true
        )
    }

    override fun runTask(
        task: FutureTask<*>
    ): EcoWrappedTaskSpigot {
        return EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTask(plugin, task)
        )
    }

    override fun runTask(
        location: Location,
        task: FutureTask<*>
    ): EcoWrappedTaskSpigot {
        return runTask(task)
    }

    override fun runTask(
        entity: Entity,
        task: FutureTask<*>
    ): EcoWrappedTaskSpigot {
        return runTask(task)
    }

    override fun runTaskAsync(task: FutureTask<*>): EcoWrappedTaskSpigot {
        return EcoWrappedTaskSpigot(
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
        )
    }

    override fun cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin)
    }
}