package com.willfp.eco.internal;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.Handler;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.ConfigHandler;
import com.willfp.eco.core.config.wrapper.ConfigFactory;
import com.willfp.eco.core.drops.DropQueueFactory;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration;
import com.willfp.eco.core.scheduling.Scheduler;
import com.willfp.eco.internal.config.EcoConfigFactory;
import com.willfp.eco.internal.config.updating.EcoConfigHandler;
import com.willfp.eco.internal.drops.EcoDropQueueFactory;
import com.willfp.eco.internal.events.EcoEventManager;
import com.willfp.eco.internal.extensions.EcoExtensionLoader;
import com.willfp.eco.internal.factory.EcoMetadataValueFactory;
import com.willfp.eco.internal.factory.EcoNamespacedKeyFactory;
import com.willfp.eco.internal.factory.EcoRunnableFactory;
import com.willfp.eco.internal.integrations.PlaceholderIntegrationPAPI;
import com.willfp.eco.internal.logging.EcoLogger;
import com.willfp.eco.internal.scheduling.EcoScheduler;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class EcoHandler extends PluginDependent<EcoPlugin> implements Handler {
    public EcoHandler(@NotNull final EcoSpigotPlugin plugin) {
        super(plugin);
    }

    @Override
    public Scheduler createScheduler(@NotNull final EcoPlugin plugin) {
        return new EcoScheduler(plugin);
    }

    @Override
    public EventManager createEventManager(@NotNull final EcoPlugin plugin) {
        return new EcoEventManager(plugin);
    }

    @Override
    public NamespacedKeyFactory createNamespacedKeyFactory(@NotNull final EcoPlugin plugin) {
        return new EcoNamespacedKeyFactory(plugin);
    }

    @Override
    public MetadataValueFactory createMetadataValueFactory(@NotNull final EcoPlugin plugin) {
        return new EcoMetadataValueFactory(plugin);
    }

    @Override
    public RunnableFactory createRunnableFactory(@NotNull final EcoPlugin plugin) {
        return new EcoRunnableFactory(plugin);
    }

    @Override
    public ExtensionLoader createExtensionLoader(@NotNull final EcoPlugin plugin) {
        return new EcoExtensionLoader(plugin);
    }

    @Override
    public ConfigHandler createConfigHandler(@NotNull final EcoPlugin plugin) {
        return new EcoConfigHandler(plugin);
    }

    @Override
    public Logger createLogger(@NotNull final EcoPlugin plugin) {
        return new EcoLogger(plugin);
    }

    @Override
    public PlaceholderIntegration createPAPIIntegration(@NotNull final EcoPlugin plugin) {
        return new PlaceholderIntegrationPAPI(plugin);
    }

    @Override
    public EcoPlugin getPlugin() {
        return super.getPlugin();
    }

    @Override
    public ConfigFactory getConfigFactory() {
        return new EcoConfigFactory();
    }

    @Override
    public DropQueueFactory getDropQueueFactory() {
        return new EcoDropQueueFactory();
    }
}
