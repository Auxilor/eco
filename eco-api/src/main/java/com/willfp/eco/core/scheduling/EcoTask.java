package com.willfp.eco.core.scheduling;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A task that has been submitted to a {@link Scheduler}.
 */
public interface EcoTask {
    /**
     * Cancel the task. Does nothing if it has already run or been cancelled.
     */
    void cancel();

    /**
     * Get if the task has been cancelled.
     *
     * @return If cancelled.
     */
    boolean isCancelled();

    /**
     * Get if the task repeats.
     *
     * @return If repeating.
     */
    boolean isRepeating();

    /**
     * Get the plugin that owns the task.
     *
     * @return The plugin.
     */
    @NotNull EcoPlugin getPlugin();

    /**
     * Get the underlying {@link BukkitTask}, for code that has not yet migrated
     * off it.
     *
     * @return The task, or null on Folia, where no {@link BukkitTask} exists.
     */
    @Nullable BukkitTask asBukkitTask();
}
