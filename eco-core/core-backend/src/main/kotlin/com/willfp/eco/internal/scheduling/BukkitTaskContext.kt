package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.eco.core.scheduling.TaskContext
import java.util.function.Consumer
import org.bukkit.Bukkit

/**
 * The only [TaskContext] Paper and Spigot need. Region and entity contexts both resolve
 * to this, because there is one thread to run on, so every call is the same
 * `BukkitScheduler` call eco has always made.
 */
open class BukkitTaskContext(
    private val plugin: EcoPlugin
) : TaskContext {
    override fun run(runnable: Runnable): EcoTask {
        val task = BukkitEcoTask(plugin, false, true)
        task.bind(Bukkit.getScheduler().runTask(plugin, runnable))
        return task
    }

    override fun runLater(runnable: Runnable, ticksLater: Long): EcoTask {
        val task = BukkitEcoTask(plugin, false, true)
        task.bind(Bukkit.getScheduler().runTaskLater(plugin, runnable, ticksLater))
        return task
    }

    override fun runTimer(runnable: Runnable, delay: Long, repeat: Long): EcoTask {
        val task = BukkitEcoTask(plugin, true, true)
        task.bind(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, repeat))
        return task
    }

    override fun runTimer(runnable: Consumer<EcoTask>, delay: Long, repeat: Long): EcoTask {
        val task = BukkitEcoTask(plugin, true, true)
        task.bind(
            Bukkit.getScheduler().runTaskTimer(plugin, Runnable { runnable.accept(task) }, delay, repeat)
        )
        return task
    }
}
