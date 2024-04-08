package com.willfp.eco.core.scheduling;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface ScheduledTask {
    @NotNull Plugin getOwningPlugin();

    boolean isRepeatingTask();

    @NotNull CancelledState cancel();

    @NotNull ExecutionState getExecutionState();

    default boolean isCancelled() {
        ExecutionState state = this.getExecutionState();
        return state == ScheduledTask.ExecutionState.CANCELLED || state == ScheduledTask.ExecutionState.CANCELLED_RUNNING;
    }

    public static enum ExecutionState {
        IDLE,
        RUNNING,
        FINISHED,
        CANCELLED,
        CANCELLED_RUNNING;
    }

    public static enum CancelledState {
        CANCELLED_BY_CALLER,
        CANCELLED_ALREADY,
        RUNNING,
        ALREADY_EXECUTED,
        NEXT_RUNS_CANCELLED,
        NEXT_RUNS_CANCELLED_ALREADY;
    }
}
