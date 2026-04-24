package com.willfp.eco.core.scheduling;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit Task handler.
 */
public interface RunnableTask extends Runnable {
    /**
     * Run the task.
     *
     * @return The created {@link EcoWrappedTask}.
     */
    @NotNull EcoWrappedTask runTask();

    /**
     * Run the task asynchronously.
     *
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskAsynchronously();

    /**
     * Run the task after a specified number of ticks.
     *
     * @param delay The number of ticks to wait.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskLater(long delay);

    /**
     * Run the task asynchronously after a specified number of ticks.
     *
     * @param delay The number of ticks to wait.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskLaterAsynchronously(long delay);

    /**
     * Run the task repeatedly on a timer.
     *
     * @param delay  The delay before the task is first ran (in ticks).
     * @param period The ticks elapsed before the task is ran again.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskTimer(long delay, long period);

    /**
     * Run the task repeatedly on a timer asynchronously.
     *
     * @param delay  The delay before the task is first ran (in ticks).
     * @param period The ticks elapsed before the task is ran again.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskTimerAsynchronously(long delay, long period);

    /**
     * Run the task at a given location.
     *
     * @param location The location to run at
     * @return The created {@link EcoWrappedTask}.
     */
    @NotNull EcoWrappedTask runTask(Location location);

    /**
     * Run the task after a specified number of ticks at a given location.
     *
     * @param location The location to run at
     * @param delay    The number of ticks to wait.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskLater(Location location, long delay);


    /**
     * Run the task repeatedly on a timer at a given location.
     *
     * @param location The location to run at
     * @param delay    The delay before the task is first ran (in ticks).
     * @param period   The ticks elapsed before the task is ran again.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskTimer(Location location, long delay, long period);

    /**
     * Run the task at a given entity.
     *
     * @param entity The entity to run for
     * @return The created {@link EcoWrappedTask}.
     */
    @NotNull EcoWrappedTask runTask(Entity entity);

    /**
     * Run the task after a specified number of ticks at a given entity.
     *
     * @param entity The entity to run for
     * @param delay  The number of ticks to wait.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskLater(Entity entity, long delay);


    /**
     * Run the task repeatedly on a timer at a given entity.
     *
     * @param entity The entity to run for
     * @param delay  The delay before the task is first ran (in ticks).
     * @param period The ticks elapsed before the task is ran again.
     * @return The created {@link EcoWrappedTask}
     */
    @NotNull EcoWrappedTask runTaskTimer(Entity entity, long delay, long period);

    /**
     * Cancel the task.
     *
     * @deprecated Use cancelTask() instead.
     */
    @Deprecated(since = "6.77.3", forRemoval = true)
    default void cancel() {
        cancelTask();
    }

    /**
     * Cancel the task.
     *
     * @return The cancelled state
     */
    EcoWrappedTask.CancelledState cancelTask();
}