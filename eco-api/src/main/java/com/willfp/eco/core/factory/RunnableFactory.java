package com.willfp.eco.core.factory;

import com.willfp.eco.core.scheduling.RunnableTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory to create runnables. Much cleaner syntax than instantiating
 * {@link org.bukkit.scheduler.BukkitRunnable}s.
 */
public interface RunnableFactory {
    /**
     * Create a {@link RunnableTask}.
     *
     * @param consumer Lambda of the code to run, where the parameter represents the instance of the runnable.
     * @return The created {@link RunnableTask}.
     */
    RunnableTask create(@NotNull Consumer<RunnableTask> consumer);
}
