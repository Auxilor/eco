package com.willfp.eco.core.factory;

import com.willfp.eco.core.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory to create runnables. Much cleaner syntax than instantiating
 * {@link Scheduler}s.
 */
public interface RunnableFactory {
    /**
     * Create a {@link Scheduler}.
     *
     * @param consumer Lambda of the code to run, where the parameter represents the instance of the runnable.
     * @return The created {@link Scheduler}.
     */
    Scheduler create(@NotNull Consumer<Scheduler> consumer);
}
