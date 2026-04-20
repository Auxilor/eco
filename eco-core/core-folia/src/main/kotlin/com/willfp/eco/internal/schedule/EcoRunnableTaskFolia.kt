package com.willfp.eco.internal.schedule

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoWrappedTask
import com.willfp.eco.core.scheduling.RunnableTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.concurrent.TimeUnit

abstract class EcoRunnableTaskFolia(protected val plugin: EcoPlugin) : RunnableTask {
    private var task: EcoWrappedTaskFolia? = null

    @Synchronized
    override fun runTask(): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(Bukkit.getGlobalRegionScheduler().run(plugin) { this.run() })
        return task!!
    }

    @Synchronized
    override fun runTaskAsynchronously(): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(Bukkit.getAsyncScheduler().runNow(plugin) { this.run() })
        return task!!
    }

    @Synchronized
    override fun runTaskLater(delay: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, { this.run() }, delay))
        return task!!
    }

    @Synchronized
    override fun runTaskLaterAsynchronously(delay: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(
            Bukkit.getAsyncScheduler().runDelayed(plugin, { this.run() }, delay * 50L, TimeUnit.MILLISECONDS)
        )
        return task!!
    }

    @Synchronized
    override fun runTaskTimer(delay: Long, period: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, { this.run() }, delay, period)
        )
        return task!!
    }

    @Synchronized
    override fun runTaskTimerAsynchronously(delay: Long, period: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(
            Bukkit.getAsyncScheduler()
                .runAtFixedRate(plugin, { this.run() }, delay * 50L, period * 50L, TimeUnit.MILLISECONDS)
        )
        return task!!
    }

    @Synchronized
    override fun runTask(location: Location): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(Bukkit.getRegionScheduler().run(plugin, location) { this.run() })
        return task!!
    }

    @Synchronized
    override fun runTaskLater(location: Location, delay: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(Bukkit.getRegionScheduler().runDelayed(plugin, location, { this.run() }, delay))
        return task!!
    }

    @Synchronized
    override fun runTaskTimer(location: Location, delay: Long, period: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(
            Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, { this.run() }, delay, period)
        )
        return task!!
    }

    @Synchronized
    override fun runTask(entity: Entity): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(entity.scheduler.run(plugin, { this.run() }, null)!!)
        return task!!
    }

    @Synchronized
    override fun runTaskLater(entity: Entity, delay: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(entity.scheduler.runDelayed(plugin, { this.run() }, null, delay)!!)
        return task!!
    }

    @Synchronized
    override fun runTaskTimer(entity: Entity, delay: Long, period: Long): EcoWrappedTaskFolia {
        task = EcoWrappedTaskFolia(entity.scheduler.runAtFixedRate(plugin, { this.run() }, null, delay, period)!!)
        return task!!
    }

    @Synchronized
    override fun cancelTask(): EcoWrappedTask.CancelledState? {
        return task?.cancelTask()
    }
}