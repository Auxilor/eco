package com.willfp.eco.internal.factory;

import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.scheduling.RunnableTask;
import com.willfp.eco.internal.scheduling.EcoRunnableTask;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EcoRunnableFactory extends PluginDependent implements RunnableFactory {
    /**
     * Factory class to produce {@link RunnableTask}s associated with an {@link EcoPlugin}.
     *
     * @param plugin The plugin that this factory creates runnables for.
     */
    public EcoRunnableFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public RunnableTask create(@NotNull final Consumer<RunnableTask> consumer) {
        return new EcoRunnableTask(this.getPlugin()) {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };
    }
}
