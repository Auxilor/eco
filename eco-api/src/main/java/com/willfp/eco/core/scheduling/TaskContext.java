package com.willfp.eco.core.scheduling;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Runs tasks in one execution context.
 * <p>
 * On Paper and Spigot every context is the main thread. On Folia a context corresponds
 * to the thread owning a region, an entity, or the global region, so the context a task
 * is submitted to determines what data it is allowed to touch.
 */
public interface TaskContext {
    /**
     * Run a task on the next tick.
     *
     * @param runnable The task.
     * @return The task handle.
     */
    @NotNull EcoTask run(@NotNull Runnable runnable);

    /**
     * Run a task after a delay.
     *
     * @param runnable   The task.
     * @param ticksLater The delay, in ticks. A delay of zero or less means the next tick.
     * @return The task handle.
     */
    @NotNull EcoTask runLater(@NotNull Runnable runnable,
                              long ticksLater);

    /**
     * Run a task after a delay.
     *
     * @param ticksLater The delay, in ticks. A delay of zero or less means the next tick.
     * @param runnable   The task.
     * @return The task handle.
     */
    @NotNull
    default EcoTask runLater(final long ticksLater,
                             @NotNull final Runnable runnable) {
        return runLater(runnable, ticksLater);
    }

    /**
     * Run a task repeatedly.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks. A period below one is treated as one.
     * @return The task handle.
     */
    @NotNull EcoTask runTimer(@NotNull Runnable runnable,
                              long delay,
                              long repeat);

    /**
     * Run a task repeatedly.
     *
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks. A period below one is treated as one.
     * @param runnable The task.
     * @return The task handle.
     */
    @NotNull
    default EcoTask runTimer(final long delay,
                             final long repeat,
                             @NotNull final Runnable runnable) {
        return runTimer(runnable, delay, repeat);
    }

    /**
     * Run a task repeatedly, passing it its own handle so that it can cancel itself.
     *
     * @param runnable The task.
     * @param delay    The delay before the first run, in ticks.
     * @param repeat   The period between runs, in ticks. A period below one is treated as one.
     * @return The task handle.
     */
    @NotNull EcoTask runTimer(@NotNull Consumer<EcoTask> runnable,
                              long delay,
                              long repeat);
}
