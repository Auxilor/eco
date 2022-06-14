package com.willfp.eco.core;

import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.config.wrapper.ConfigFactory;
import com.willfp.eco.core.data.ExtendedPersistentDataContainer;
import com.willfp.eco.core.data.ProfileHandler;
import com.willfp.eco.core.data.keys.KeyRegistry;
import com.willfp.eco.core.drops.DropQueueFactory;
import com.willfp.eco.core.entities.ai.EntityController;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.gui.GUIFactory;
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration;
import com.willfp.eco.core.items.SNBTHandler;
import com.willfp.eco.core.proxy.Cleaner;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.scheduling.Scheduler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

/**
 * @see Eco#getHandler()
 */
@ApiStatus.Internal
public interface Handler {
    /**
     * Create a scheduler.
     *
     * @param plugin The plugin.
     * @return The scheduler.
     */
    @NotNull
    Scheduler createScheduler(@NotNull EcoPlugin plugin);

    /**
     * Create an event manager.
     *
     * @param plugin The plugin.
     * @return The event manager.
     */
    @NotNull
    EventManager createEventManager(@NotNull EcoPlugin plugin);

    /**
     * Create a NamespacedKey factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    @NotNull
    NamespacedKeyFactory createNamespacedKeyFactory(@NotNull EcoPlugin plugin);

    /**
     * Create a MetadataValue factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    @NotNull
    MetadataValueFactory createMetadataValueFactory(@NotNull EcoPlugin plugin);

    /**
     * Create a Runnable factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    @NotNull
    RunnableFactory createRunnableFactory(@NotNull EcoPlugin plugin);

    /**
     * Create an ExtensionLoader.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    @NotNull
    ExtensionLoader createExtensionLoader(@NotNull EcoPlugin plugin);

    /**
     * Create a config handler.
     *
     * @param plugin The plugin.
     * @return The handler.
     */
    @NotNull
    ConfigHandler createConfigHandler(@NotNull EcoPlugin plugin);

    /**
     * Create a logger.
     *
     * @param plugin The plugin.
     * @return The logger.
     */
    @NotNull
    Logger createLogger(@NotNull EcoPlugin plugin);

    /**
     * Create a PAPI integration.
     *
     * @param plugin The plugin.
     * @return The integration.
     */
    @NotNull
    PlaceholderIntegration createPAPIIntegration(@NotNull EcoPlugin plugin);

    /**
     * Create a proxy factory.
     *
     * @param plugin The plugin.
     * @return The factory.
     */
    @NotNull
    ProxyFactory createProxyFactory(@NotNull EcoPlugin plugin);

    /**
     * Get eco Spigot plugin.
     *
     * @return The plugin.
     */
    @NotNull
    EcoPlugin getEcoPlugin();

    /**
     * Get config factory.
     *
     * @return The factory.
     */
    @NotNull
    ConfigFactory getConfigFactory();

    /**
     * Get drop queue factory.
     *
     * @return The factory.
     */
    @NotNull
    DropQueueFactory getDropQueueFactory();

    /**
     * Get GUI factory.
     *
     * @return The factory.
     */
    @NotNull
    GUIFactory getGUIFactory();

    /**
     * Get cleaner.
     *
     * @return The cleaner.
     */
    @NotNull
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
    @Nullable
    EcoPlugin getPluginByName(@NotNull String name);

    /**
     * Get all loaded eco plugins.
     *
     * @return A list of plugin names in lowercase.
     */
    @NotNull
    List<String> getLoadedPlugins();

    /**
     * Create a FastItemStack.
     *
     * @param itemStack The base ItemStack.
     * @return The FastItemStack.
     */
    @NotNull
    FastItemStack createFastItemStack(@NotNull ItemStack itemStack);

    /**
     * Register bStats metrics.
     *
     * @param plugin The plugin.
     */
    void registerBStats(@NotNull EcoPlugin plugin);

    /**
     * Get Adventure audiences.
     *
     * @return The audiences.
     */
    @Nullable
    BukkitAudiences getAdventure();

    /**
     * Get the key registry.
     *
     * @return The registry.
     */
    @NotNull
    KeyRegistry getKeyRegistry();

    /**
     * Get the PlayerProfile handler.
     *
     * @return The handler.
     */
    @NotNull
    ProfileHandler getProfileHandler();

    /**
     * Create dummy entity - never spawned, exists purely in code.
     *
     * @param location The location.
     * @return The entity.
     */
    @NotNull
    Entity createDummyEntity(@NotNull Location location);

    /**
     * Create a {@link NamespacedKey} quickly
     * <p>
     * Bypasses the constructor, allowing for the creation of invalid keys,
     * therefore this is considered unsafe and should only be called after
     * the key has been confirmed to be valid.
     *
     * @param namespace The namespace.
     * @param key       The key.
     * @return The key.
     */
    @NotNull
    NamespacedKey createNamespacedKey(@NotNull String namespace,
                                      @NotNull String key);

    /**
     * Return or get props for a plugin.
     *
     * @param existing The existing constructor props.
     * @param plugin   The plugin.
     * @return The props.
     */
    @NotNull
    PluginProps getProps(@Nullable PluginProps existing,
                         @NotNull Class<? extends EcoPlugin> plugin);

    /**
     * Format a string with MiniMessage.
     *
     * @param message The message.
     * @return The formatted string.
     */
    @NotNull
    String formatMiniMessage(@NotNull String message);

    /**
     * Create controlled entity from a mob.
     *
     * @param mob The mob.
     * @param <T> The mob type.
     * @return The controlled entity.
     */
    @NotNull <T extends Mob> EntityController<T> createEntityController(@NotNull T mob);

    /**
     * Adapt base PDC to extended PDC.
     *
     * @param container The container.
     * @return The extended container.
     */
    @NotNull
    ExtendedPersistentDataContainer adaptPdc(@NotNull PersistentDataContainer container);

    /**
     * Create new PDC.
     *
     * @return The container.
     */
    @NotNull
    PersistentDataContainer newPdc();

    /**
     * Get SNBT handler.
     *
     * @return The SNBT handler.
     */
    @NotNull
    SNBTHandler getSNBTHandler();
}
