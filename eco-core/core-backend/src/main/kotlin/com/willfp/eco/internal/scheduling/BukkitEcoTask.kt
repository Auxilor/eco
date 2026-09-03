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
    private val repeating: Boolean,
    private val sync: Boolean
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

    /**
     * The real Bukkit ID once bound. -1 before then, and after a task that was cancelled
     * before it could be submitted, because there is no ID to report.
     */
    @Deprecated("Hold the EcoTask and cancel that instead.")
    @Suppress("DEPRECATION")
    override fun getTaskId(): Int = handle?.taskId ?: -1

    @Deprecated("Ask the context the task was submitted through instead.")
    @Suppress("DEPRECATION")
    override fun isSync(): Boolean = sync

    @Deprecated("An EcoTask is already a BukkitTask.")
    @Suppress("DEPRECATION")
    override fun asBukkitTask(): BukkitTask = this
}
