package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.scheduling.EcoTask
import org.bukkit.scheduler.BukkitTask

/**
 * An [EcoTask] backed by a [BukkitTask].
 *
 * The handle is created before the task is submitted, so that a repeating task can be
 * handed its own handle, and is bound afterwards. Cancelling an unbound handle is
 * remembered and applied on [bind].
 */
class BukkitEcoTask(
    private val plugin: EcoPlugin,
    private val repeating: Boolean
) : EcoTask {
    @Volatile
    private var handle: BukkitTask? = null

    @Volatile
    private var cancelRequested = false

    fun bind(task: BukkitTask?) {
        if (task == null) {
            cancelRequested = true
            return
        }

        handle = task

        if (cancelRequested) {
            task.cancel()
        }
    }

    override fun cancel() {
        cancelRequested = true
        handle?.cancel()
    }

    override fun isCancelled(): Boolean = cancelRequested || (handle?.isCancelled ?: false)

    override fun isRepeating(): Boolean = repeating

    override fun getPlugin(): EcoPlugin = plugin

    override fun asBukkitTask(): BukkitTask? = handle
}
