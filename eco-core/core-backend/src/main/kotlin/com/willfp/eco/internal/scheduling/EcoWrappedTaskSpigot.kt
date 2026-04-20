package com.willfp.eco.internal.scheduling

import com.willfp.eco.core.scheduling.EcoWrappedTask
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class EcoWrappedTaskSpigot(val task: BukkitTask, val repeatable: Boolean = true) : EcoWrappedTask, BukkitTask {
    @Deprecated("To be removed in the future.")
    override fun getTaskId(): Int {
        return task.taskId
    }

    @Deprecated("Use getOwningPlugin() instead.")
    override fun getOwner(): Plugin {
        return this.getOwningPlugin()
    }

    @Deprecated("Use isAsync() instead.")
    override fun isSync(): Boolean {
        return task.isSync
    }

    override fun getOwningPlugin(): Plugin {
        return task.owner
    }

    override fun isAsync(): Boolean {
        return !task.isSync
    }

    override fun isRepeatingTask(): Boolean {
        return repeatable
    }

    override fun isCancelled(): Boolean {
        return super.isCancelled()
    }

    override fun getExecutionState(): EcoWrappedTask.ExecutionState = when (task.isCancelled) {
        true -> EcoWrappedTask.ExecutionState.CANCELLED
        false -> EcoWrappedTask.ExecutionState.RUNNING
    }

    override fun cancelTask(): EcoWrappedTask.CancelledState =
        when (task.cancel().run { task.isCancelled }) {
            true -> EcoWrappedTask.CancelledState.CANCELLED_ALREADY
            false -> EcoWrappedTask.CancelledState.RUNNING
        }

    @Deprecated("Use cancelTask() instead.")
    override fun cancel() {
        this.cancelTask()
    }
}