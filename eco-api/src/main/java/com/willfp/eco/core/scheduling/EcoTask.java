package com.willfp.eco.core.scheduling;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * A task that has been submitted to a {@link Scheduler}.
 * <p>
 * This extends {@link BukkitTask} purely for binary compatibility, and not because an
 * {@link EcoTask} is meaningfully a Bukkit task. Plugins compiled against eco 6 hold the
 * results of {@link Scheduler#runLater(Runnable, long)} and friends in {@link BukkitTask}
 * fields and locals, and those call sites are frozen in already-published jars. Widening
 * {@link EcoTask} to a {@link BukkitTask} makes each of those methods a covariant
 * override of its {@link LegacyScheduler} counterpart, which is what lets the compiler
 * emit the old descriptor as a bridge method and keeps those jars loading.
 * <p>
 * The {@link BukkitTask} members are re-declared below so that they can carry
 * deprecation: an inherited method cannot be deprecated where it is inherited. All of
 * them, and the {@code extends BukkitTask} itself, are removed together with
 * {@link LegacyScheduler}.
 */
public interface EcoTask extends BukkitTask {
    /**
     * Cancel the task. Does nothing if it has already run or been cancelled.
     */
    @Override
    void cancel();

    /**
     * Get if the task has been cancelled.
     *
     * @return If cancelled.
     */
    @Override
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
     * Get the Bukkit task ID.
     *
     * @return The task ID, or -1 where the task has no ID.
     * @deprecated Hold the {@link EcoTask} itself and call {@link #cancel()}. Folia has no
     *         task ID space, so this returns -1 there, and passing that to
     *         {@link org.bukkit.scheduler.BukkitScheduler#cancelTask(int)} does nothing.
     */
    @Deprecated(forRemoval = true)
    @Override
    default int getTaskId() {
        return -1;
    }

    /**
     * Get the plugin that owns the task.
     *
     * @return The plugin.
     * @deprecated Use {@link #getPlugin()}, which is typed as an {@link EcoPlugin}.
     */
    @Deprecated(forRemoval = true)
    @Override
    @NotNull
    default Plugin getOwner() {
        return getPlugin();
    }

    /**
     * Get if the task runs on a server thread rather than off-thread.
     * <p>
     * True for every context except {@link Scheduler#async()}. On Folia this says only
     * that the task runs on some server thread, not which region owns it.
     *
     * @return If the task is synchronous.
     * @deprecated Ask the context the task was submitted through instead.
     */
    @Deprecated(forRemoval = true)
    @Override
    boolean isSync();

    /**
     * Get this task as a {@link BukkitTask}.
     *
     * @return This task.
     * @deprecated An {@link EcoTask} is now itself a {@link BukkitTask}, so this returns
     *         {@code this} and never null. Use the {@link EcoTask} directly.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default BukkitTask asBukkitTask() {
        return this;
    }
}
