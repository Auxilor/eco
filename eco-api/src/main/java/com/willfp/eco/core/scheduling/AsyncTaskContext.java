package com.willfp.eco.core.scheduling;

import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * Runs tasks off the server threads. Tasks submitted here must never touch world or
 * entity state.
 * <p>
 * Note that tick-based delays behave differently across platforms: on Paper and Spigot
 * they are driven by the server tick and drift when the server lags, while on Folia they
 * are wall-clock, because Folia's async scheduler has no notion of ticks. Use the
 * {@link TimeUnit} overloads where that difference matters.
 */
public interface AsyncTaskContext extends TaskContext {
    /**
     * Run a task after a delay.
     *
     * @param runnable The task.
     * @param delay    The delay.
     * @param unit     The unit of the delay.
     * @return The task handle.
     */
    @NotNull EcoTask runLater(@NotNull Runnable runnable,
                              long delay,
                              @NotNull TimeUnit unit);

    /**
     * Run a task repeatedly.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run.
     * @param period   The period between runs.
     * @param unit     The unit of the delay and period.
     * @return The task handle.
     */
    @NotNull EcoTask runTimer(@NotNull Runnable runnable,
                              long delay,
                              long period,
                              @NotNull TimeUnit unit);
}
