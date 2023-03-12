package com.willfp.eco.core.scheduling;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Thread scheduler to handle tasks and asynchronous code.
 */
public interface Scheduler {
    /**
     * Run the task after a specified tick delay.
     *
     * @param runnable   The lambda to run.
     * @param ticksLater The amount of ticks to wait before execution.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runLater(@NotNull Runnable runnable,
                                long ticksLater) {
        return runLater(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), (int) ticksLater, runnable);
    }

    /**
     * Run the task after a specified tick delay.
     * <p>
     * Reordered for better kotlin interop.
     *
     * @param runnable   The lambda to run.
     * @param ticksLater The amount of ticks to wait before execution.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runLater(long ticksLater,
                                @NotNull Runnable runnable) {
        return runLater(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), (int) ticksLater, runnable);
    }

    /**
     * Run the task repeatedly on a timer.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runTimer(@NotNull Runnable runnable,
                                long delay,
                                long repeat) {
        return runTimer(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), (int) delay, (int) repeat, runnable);
    }

    /**
     * Run the task repeatedly on a timer.
     * <p>
     * Reordered for better kotlin interop.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runTimer(long delay,
                                long repeat,
                                @NotNull Runnable runnable) {
        return runTimer(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), (int) delay, (int) repeat, runnable);
    }

    /**
     * Run the task repeatedly and asynchronously on a timer.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runAsyncTimer(@NotNull Runnable runnable,
                                     long delay,
                                     long repeat) {
        return runTimerAsync((int) delay, (int) repeat, runnable);
    }

    /**
     * Run the task repeatedly and asynchronously on a timer.
     * <p>
     * Reordered for better kotlin interop.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask runAsyncTimer(long delay,
                                     long repeat,
                                     @NotNull Runnable runnable) {
        return runTimerAsync((int) delay, (int) repeat, runnable);
    }

    /**
     * Run the task.
     *
     * @param runnable The lambda to run.
     * @return The created {@link BukkitTask}.
     * @deprecated Does not work with Folia.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default BukkitTask run(@NotNull Runnable runnable) {
        return run(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), runnable);
    }

    /**
     * Schedule the task to be run repeatedly on a timer.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The id of the task.
     * @deprecated Not needed.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default int syncRepeating(@NotNull Runnable runnable,
                              long delay,
                              long repeat) {
        return runTimer(runnable, delay, repeat).getTaskId();
    }

    /**
     * Schedule the task to be ran repeatedly on a timer.
     * <p>
     * Reordered for better kotlin interop.
     *
     * @param runnable The lambda to run.
     * @param delay    The amount of ticks to wait before the first execution.
     * @param repeat   The amount of ticks to wait between executions.
     * @return The id of the task.
     * @deprecated Not needed.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    default int syncRepeating(long delay,
                              long repeat,
                              @NotNull Runnable runnable) {
        return runTimer(runnable, delay, repeat).getTaskId();
    }

    /**
     * Cancel all running tasks from the linked {@link EcoPlugin}.
     */
    void cancelAll();

    /**
     * Run a task asynchronously.
     *
     * @param task The lambda to run.
     * @return The created {@link BukkitTask}.
     */
    BukkitTask runAsync(@NotNull Runnable task);

    /**
     * Run a task.
     *
     * @param location The location.
     * @param task     The task.
     * @return The created {@link BukkitTask}.
     */
    BukkitTask run(@NotNull Location location,
                   @NotNull Runnable task);

    /**
     * Run a task after a delay.
     *
     * @param location   The location.
     * @param ticksLater The delay.
     * @param task       The task.
     * @return The created {@link BukkitTask}.
     */
    BukkitTask runLater(@NotNull Location location,
                        int ticksLater,
                        @NotNull Runnable task);

    /**
     * Run a task on a timer.
     *
     * @param location The location.
     * @param delay    The delay.
     * @param repeat   The repeat delay.
     * @param task     The task.
     * @return The created {@link BukkitTask}.
     */
    BukkitTask runTimer(@NotNull Location location,
                        int delay,
                        int repeat,
                        @NotNull Runnable task);

    /**
     * Run a task asynchronously on a timer.
     *
     * @param delay  The delay.
     * @param repeat The repeat delay.
     * @param task   The task.
     * @return The created {@link BukkitTask}.
     */
    BukkitTask runTimerAsync(int delay,
                             int repeat,
                             @NotNull Runnable task);
}
