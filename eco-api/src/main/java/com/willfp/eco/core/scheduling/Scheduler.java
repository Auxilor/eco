package com.willfp.eco.core.scheduling;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Schedules tasks for an {@link EcoPlugin}.
 * <p>
 * Tasks are submitted through a context, which says what the task is allowed to touch.
 * On Paper and Spigot every context is the main thread. On Folia the context decides
 * which thread the task runs on, and a task that touches data outside its context will
 * fail, so pick the context that owns the data:
 * <ul>
 *     <li>{@link #global()} for data owned by nothing in particular: configs, databases,
 *     plugin state.</li>
 *     <li>{@link #at(Location)} for blocks and world state.</li>
 *     <li>{@link #on(Entity)} for entities, including players and their inventories.</li>
 *     <li>{@link #async()} for work that touches neither.</li>
 * </ul>
 */
public interface Scheduler {
    /**
     * Get the context for tasks with no region affinity.
     *
     * @return The context.
     */
    @NotNull TaskContext global();

    /**
     * Get the context for tasks touching blocks or world state at a location.
     *
     * @param location The location.
     * @return The context.
     */
    @NotNull TaskContext at(@NotNull Location location);

    /**
     * Get the context for tasks touching blocks or world state in a chunk.
     *
     * @param world  The world.
     * @param chunkX The chunk x coordinate.
     * @param chunkZ The chunk z coordinate.
     * @return The context.
     */
    @NotNull TaskContext at(@NotNull World world,
                            int chunkX,
                            int chunkZ);

    /**
     * Get the context for tasks touching an entity. The context follows the entity if it
     * moves between regions.
     *
     * @param entity The entity.
     * @return The context.
     */
    @NotNull EntityTaskContext on(@NotNull Entity entity);

    /**
     * Get the context for tasks that touch neither world nor entity state.
     *
     * @return The context.
     */
    @NotNull AsyncTaskContext async();

    /**
     * Cancel every task submitted through this scheduler.
     */
    void cancelAll();

    /**
     * Run a task after a delay, with no region affinity.
     *
     * @param runnable   The task.
     * @param ticksLater The delay, in ticks.
     * @return The task handle.
     * @deprecated Use {@link #global()}, or a context that owns the data the task
     *         touches. Implicitly global scheduling cannot be made correct on Folia.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runLater(@NotNull final Runnable runnable,
                             final long ticksLater) {
        return global().runLater(runnable, ticksLater);
    }

    /**
     * Run a task after a delay, with no region affinity.
     *
     * @param ticksLater The delay, in ticks.
     * @param runnable   The task.
     * @return The task handle.
     * @deprecated Use {@link #global()}, or a context that owns the data the task touches.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runLater(final long ticksLater,
                             @NotNull final Runnable runnable) {
        return global().runLater(runnable, ticksLater);
    }

    /**
     * Run a task repeatedly, with no region affinity.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @return The task handle.
     * @deprecated Use {@link #global()}, or a context that owns the data the task touches.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runTimer(@NotNull final Runnable runnable,
                             final long delay,
                             final long repeat) {
        return global().runTimer(runnable, delay, repeat);
    }

    /**
     * Run a task repeatedly, with no region affinity.
     *
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @param runnable The task.
     * @return The task handle.
     * @deprecated Use {@link #global()}, or a context that owns the data the task touches.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runTimer(final long delay,
                             final long repeat,
                             @NotNull final Runnable runnable) {
        return global().runTimer(runnable, delay, repeat);
    }

    /**
     * Run a task off-thread repeatedly.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @return The task handle.
     * @deprecated Use {@link #async()}.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runAsyncTimer(@NotNull final Runnable runnable,
                                  final long delay,
                                  final long repeat) {
        return async().runTimer(runnable, delay, repeat);
    }

    /**
     * Run a task off-thread repeatedly.
     *
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks.
     * @param runnable The task.
     * @return The task handle.
     * @deprecated Use {@link #async()}.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runAsyncTimer(final long delay,
                                  final long repeat,
                                  @NotNull final Runnable runnable) {
        return async().runTimer(runnable, delay, repeat);
    }

    /**
     * Run a task on the next tick, with no region affinity.
     *
     * @param runnable The task.
     * @return The task handle.
     * @deprecated Use {@link #global()}, or a context that owns the data the task touches.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask run(@NotNull final Runnable runnable) {
        return global().run(runnable);
    }

    /**
     * Run a task off-thread.
     *
     * @param runnable The task.
     * @return The task handle.
     * @deprecated Use {@link #async()}.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    default EcoTask runAsync(@NotNull final Runnable runnable) {
        return async().run(runnable);
    }
}
