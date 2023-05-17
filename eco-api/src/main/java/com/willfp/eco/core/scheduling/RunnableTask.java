package com.willfp.eco.core.scheduling;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit Task handler.
 */
public interface RunnableTask extends Runnable {
    /**
     * Run the task.
     *
     * @return The created {@link BukkitTask}.
     */
    @NotNull Scheduler runTask();

    /**
     * Run the task asynchronously.
     *
     * @return The created {@link BukkitTask}
     */
    @NotNull Scheduler runTaskAsynchronously();

    /**
     * Run the task after a specified number of ticks.
     *
     * @param delay The number of ticks to wait.
     * @return The created {@link BukkitTask}
     */
    @NotNull Scheduler runTaskLater(long delay);

    /**
     * Run the task asynchronously after a specified number of ticks.
     *
     * @param delay The number of ticks to wait.
     * @return The created {@link BukkitTask}
     */
    @NotNull Scheduler runTaskLaterAsynchronously(long delay);

    /**
     * Run the task repeatedly on a timer.
     *
     * @param delay  The delay before the task is first ran (in ticks).
     * @param period The ticks elapsed before the task is ran again.
     * @return The created {@link BukkitTask}
     */
    @NotNull Scheduler runTaskTimer(long delay, long period);

    /**
     * Run the task repeatedly on a timer asynchronously.
     *
     * @param delay  The delay before the task is first ran (in ticks).
     * @param period The ticks elapsed before the task is ran again.
     * @return The created {@link BukkitTask}
     */
    @NotNull Scheduler runTaskTimerAsynchronously(long delay, long period);

    /**
     * Cancel the task.
     */
    void cancel();
}
