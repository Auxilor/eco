package com.willfp.eco.core.scheduling;

import org.bukkit.scheduler.BukkitTask;

/**
 * The eco 6 scheduling API, preserved for binary compatibility.
 * <p>
 * Every method here is overridden covariantly by {@link Scheduler}, which returns an
 * {@link EcoTask} instead of a {@link BukkitTask}. The compiler turns each of those
 * overrides into a bridge method carrying the descriptor declared here, so call sites in
 * plugins compiled against eco 6 keep resolving after those plugins are never recompiled.
 * <p>
 * Nothing should reference this interface. It exists to hold descriptors, not to be used:
 * source-level calls resolve to {@link Scheduler}'s overrides, which carry their own
 * deprecation, and the bridges the compiler emits from these declarations are synthetic
 * and therefore invisible to both javac and kotlinc.
 *
 * @deprecated Use {@link Scheduler} and its task contexts. Removed once plugins compiled
 *         against eco 6 are no longer supported, together with {@code EcoTask extends
 *         BukkitTask} and the deprecated members that widening brings with it.
 */
@Deprecated(forRemoval = true)
public interface LegacyScheduler {
    /**
     * Run a task after a delay.
     *
     * @param runnable   The task.
     * @param ticksLater The delay, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runLater(Runnable runnable,
                        long ticksLater);

    /**
     * Run a task after a delay.
     *
     * @param ticksLater The delay, in ticks.
     * @param runnable   The task.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runLater(long ticksLater,
                        Runnable runnable);

    /**
     * Run a task repeatedly.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTimer(Runnable runnable,
                        long delay,
                        long repeat);

    /**
     * Run a task repeatedly.
     *
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @param runnable The task.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runTimer(long delay,
                        long repeat,
                        Runnable runnable);

    /**
     * Run a task off-thread repeatedly.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runAsyncTimer(Runnable runnable,
                             long delay,
                             long repeat);

    /**
     * Run a task off-thread repeatedly.
     *
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @param runnable The task.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runAsyncTimer(long delay,
                             long repeat,
                             Runnable runnable);

    /**
     * Run a task on the next tick.
     *
     * @param runnable The task.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask run(Runnable runnable);

    /**
     * Run a task off-thread.
     *
     * @param runnable The task.
     * @return The task.
     * @deprecated See {@link LegacyScheduler}.
     */
    @Deprecated(forRemoval = true)
    BukkitTask runAsync(Runnable runnable);
}
