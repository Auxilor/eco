package com.willfp.eco.internal.factory;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.scheduling.RunnableTask;
import com.willfp.eco.internal.scheduling.EcoRunnableTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EcoRunnableFactory extends PluginDependent<EcoPlugin> implements RunnableFactory {
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
