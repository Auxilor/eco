package com.willfp.eco.core.scheduling;

import org.bukkit.scheduler.BukkitTask;

/**
 * The eco 6 {@link RunnableTask} API, preserved for binary compatibility.
 * <p>
 * Serves the same purpose as {@link LegacyScheduler}: every method here is overridden
 * covariantly by {@link RunnableTask} to return an {@link EcoTask}, so the compiler emits
 * the eco 6 descriptor as a bridge and call sites in already-published jars keep
 * resolving. {@link #cancel()} is declared here rather than on {@link RunnableTask}
 * because it moved to {@link EcoTask}, and its descriptor has no return type to bridge.
 *
 * @deprecated Use {@link Scheduler} and its task contexts. Removed alongside
 *         {@link LegacyScheduler}.
 */
@Deprecated(forRemoval = true)
public interface LegacyRunnableTask {
    /**
     * Run the task on the next tick.
     *
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTask();

    /**
     * Run the task off-thread.
     *
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTaskAsynchronously();

    /**
     * Run the task after a delay.
     *
     * @param delay The delay, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTaskLater(long delay);

    /**
     * Run the task off-thread after a delay.
     *
     * @param delay The delay, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTaskLaterAsynchronously(long delay);

    /**
     * Run the task repeatedly.
     *
     * @param delay  The delay before the first run, in ticks.
     * @param period The period between runs, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTaskTimer(long delay,
                            long period);

    /**
     * Run the task off-thread repeatedly.
     *
     * @param delay  The delay before the first run, in ticks.
     * @param period The period between runs, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyRunnableTask}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTaskTimerAsynchronously(long delay,
                                          long period);

    /**
     * Cancel the task, if it has been submitted.
     *
     * @deprecated Cancel the {@link EcoTask} returned when the task was submitted.
     */
    @Deprecated(forRemoval = true)
    void cancel();
}
