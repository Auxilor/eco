package com.willfp.eco.core;

import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.config.wrapper.ConfigFactory;
import com.willfp.eco.core.drops.DropQueueFactory;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.gui.GUIFactory;
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration;
import com.willfp.eco.core.proxy.Cleaner;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.scheduling.Scheduler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

/**
 * @see Eco
 */
public interface Handler {
    /**
     * Create a scheduler.
     *
     * @param plugin The plugin.
     * @return The scheduler.
     */
    Scheduler createScheduler(@NotNull EcoPlugin plugin);

    /**
     * Create an event manager.
     *
     * @param plugin The plugin.
     * @return The event manager.
     */
    EventManager createEventManager(@NotNull EcoPlugin plugin);

    /**
     * Create a NamespacedKey factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    NamespacedKeyFactory createNamespacedKeyFactory(@NotNull EcoPlugin plugin);

    /**
     * Create a MetadataValue factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    MetadataValueFactory createMetadataValueFactory(@NotNull EcoPlugin plugin);

    /**
     * Create a Runnable factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    RunnableFactory createRunnableFactory(@NotNull EcoPlugin plugin);

    /**
     * Create an ExtensionLoader.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    ExtensionLoader createExtensionLoader(@NotNull EcoPlugin plugin);

    /**
     * Create a config handler.
     *
     * @param plugin The plugin.
     * @return The handler.
     */
    ConfigHandler createConfigHandler(@NotNull EcoPlugin plugin);

    /**
     * Create a logger.
     *
     * @param plugin The plugin.
     * @return The logger.
     */
    Logger createLogger(@NotNull EcoPlugin plugin);

    /**
     * Create a PAPI integration.
     *
     * @param plugin The plugin.
     * @return The integration.
     */
    PlaceholderIntegration createPAPIIntegration(@NotNull EcoPlugin plugin);

    /**
     * Create a proxy factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    ProxyFactory createProxyFactory(@NotNull EcoPlugin plugin);

    /**
     * Get eco Spigot plugin.
     *
     * @return The plugin.
     */
    EcoPlugin getEcoPlugin();

    /**
     * Get config factory.
     *
     * @return The factory.
     */
    ConfigFactory getConfigFactory();

    /**
     * Get drop queue factory.
     *
     * @return The factory.
     */
    DropQueueFactory getDropQueueFactory();

    /**
     * Get GUI factory.
     *
     * @return The factory.
     */
    GUIFactory getGUIFactory();

    /**
     * Get cleaner.
     *
     * @return The cleaner.
     */
    Cleaner getCleaner();

    /**
     * Add new plugin.
     *
     * @param plugin The plugin.
     */
    void addNewPlugin(@NotNull EcoPlugin plugin);

    /**
     * Get plugin by name.
     *
     * @param name The name.
     * @return The plugin.
     */
    EcoPlugin getPluginByName(@NotNull String name);

    /**
     * Get all loaded eco plugins.
     *
     * @return A list of plugin names in lowercase.
     */
    List<String> getLoadedPlugins();

    /**
     * Create a FastItemStack.
     *
     * @param itemStack The base ItemStack.
     * @return The FastItemStack.
     */
    FastItemStack createFastItemStack(@NotNull ItemStack itemStack);

    /**
     * Register bStats metrics.
     *
     * @param plugin The plugin.
     */
    void registerBStats(@NotNull EcoPlugin plugin);
}
