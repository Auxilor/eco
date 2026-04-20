package com.willfp.eco.internal.schedule

import com.willfp.eco.core.scheduling.EcoWrappedTask
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.plugin.Plugin

class EcoWrappedTaskFolia(val task: ScheduledTask) : EcoWrappedTask {
    val clazz =
        runCatching { Class.forName("io.papermc.paper.threadedregions.scheduler.FoliaAsyncScheduler.AsyncScheduledTask") }.getOrNull()

    override fun getOwningPlugin(): Plugin {
        return task.owningPlugin
    }

    override fun isAsync(): Boolean {
        return this.clazz != null && this.clazz.isAssignableFrom(task::class.java)
    }

    override fun isRepeatingTask(): Boolean {
        return task.isRepeatingTask
    }

    override fun getExecutionState(): EcoWrappedTask.ExecutionState =
        when (task.executionState) {
            ScheduledTask.ExecutionState.IDLE -> EcoWrappedTask.ExecutionState.IDLE
            ScheduledTask.ExecutionState.RUNNING -> EcoWrappedTask.ExecutionState.RUNNING
            ScheduledTask.ExecutionState.FINISHED -> EcoWrappedTask.ExecutionState.FINISHED
            ScheduledTask.ExecutionState.CANCELLED -> EcoWrappedTask.ExecutionState.CANCELLED
            ScheduledTask.ExecutionState.CANCELLED_RUNNING -> EcoWrappedTask.ExecutionState.CANCELLED_RUNNING
        }

    override fun cancelTask(): EcoWrappedTask.CancelledState =
        when (task.cancel()) {
            ScheduledTask.CancelledState.CANCELLED_BY_CALLER -> EcoWrappedTask.CancelledState.CANCELLED_BY_CALLER
            ScheduledTask.CancelledState.CANCELLED_ALREADY -> EcoWrappedTask.CancelledState.CANCELLED_ALREADY
            ScheduledTask.CancelledState.RUNNING -> EcoWrappedTask.CancelledState.RUNNING
            ScheduledTask.CancelledState.ALREADY_EXECUTED -> EcoWrappedTask.CancelledState.ALREADY_EXECUTED
            ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED -> EcoWrappedTask.CancelledState.NEXT_RUNS_CANCELLED
            ScheduledTask.CancelledState.NEXT_RUNS_CANCELLED_ALREADY -> EcoWrappedTask.CancelledState.NEXT_RUNS_CANCELLED_ALREADY
        }
}