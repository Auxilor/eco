package com.willfp.eco.core.scheduling

import org.bukkit.plugin.Plugin

interface EcoWrappedTask {
    fun getOwningPlugin(): Plugin

    /**
     * Whether the task is async or not.
     * <p>
     * Async tasks are never run on any world threads, including on Folia.
     *
     * @return true if the task is async
     */
    fun isAsync(): Boolean {
        return false
    }

    /**
     * Whether the task is repeating or not.
     *
     * @return true if the task is repeating
     */
    fun isRepeatingTask(): Boolean {
        return false
    }

    /**
     * The execution state of the task.
     *
     * @return the execution state
     */
    fun getExecutionState(): ExecutionState

    /**
     * Cancels the task.
     * @deprecated Use cancelTask() instead.
     */
    @Deprecated("Use cancelTask() instead.")
    fun cancel() {
        this.cancelTask()
    }

    /**
     * Cancels the task.
     */
    fun cancelTask(): CancelledState

    /**
     * Whether the task is cancelled or not.
     *
     * @return true if the task is cancelled
     */
    fun isCancelled(): Boolean {
        val state = this.getExecutionState()
        return state == ExecutionState.CANCELLED || state == ExecutionState.CANCELLED_RUNNING
    }

    enum class ExecutionState {
        IDLE,
        RUNNING,
        FINISHED,
        CANCELLED,
        CANCELLED_RUNNING;
    }

    enum class CancelledState {
        CANCELLED_BY_CALLER,
        CANCELLED_ALREADY,
        RUNNING,
        ALREADY_EXECUTED,
        NEXT_RUNS_CANCELLED,
        NEXT_RUNS_CANCELLED_ALREADY;
    }
}
