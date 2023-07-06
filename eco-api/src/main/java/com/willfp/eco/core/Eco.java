package com.willfp.eco.core;

import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.PluginCommandBase;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.data.ExtendedPersistentDataContainer;
import com.willfp.eco.core.data.PlayerProfile;
import com.willfp.eco.core.data.ServerProfile;
import com.willfp.eco.core.data.keys.PersistentDataKey;
import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.core.entities.ai.EntityController;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.menu.MenuType;
import com.willfp.eco.core.gui.slot.SlotBuilder;
import com.willfp.eco.core.gui.slot.functional.SlotProvider;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.core.packet.Packet;
import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.scheduling.Scheduler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Holds the instance of eco for bridging between the frontend and backend.
 * <p>
 * <strong>Do not use this in your plugins!</strong> It can and will contain
 * breaking changes between minor versions and even patches, and you will create compatibility
 * issues by. All parts of this have been abstracted into logically named API components that you
 * can use.
 *
 * @see Eco#get()
 */
@ApiStatus.Internal
public interface Eco {

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
     * @param plugin The plugin.F
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
     * Get NOOP logger.
     *
     * @return The logger.
     */
    @NotNull
    Logger getNOOPLogger();

    /**
     * Create a PAPI integration.
     *
     * @param plugin The plugin.
     */
    void createPAPIIntegration(@NotNull EcoPlugin plugin);

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
     * Create PluginCommandBase implementation of {@link PluginCommand}.
     *
     * @param parentDelegate the enclosing class of this implementation.
     * @param plugin         the plugin.
     * @param name           the name of the command.
     * @param permission     the permission of the command.
     * @param playersOnly    if the command is players only.
     * @return The PluginCommandBase implementation
     */
    @NotNull
    PluginCommandBase createPluginCommand(@NotNull PluginCommandBase parentDelegate,
                                          @NotNull EcoPlugin plugin,
                                          @NotNull String name,
                                          @NotNull String permission,
                                          boolean playersOnly);

    /**
     * Create CommandBase implementation of {@link com.willfp.eco.core.command.impl.Subcommand Subcommand}.
     *
     * @param parentDelegate the enclosing class of this implementation.
     * @param plugin         the plugin.
     * @param name           the name of the command.
     * @param permission     the permission of the command.
     * @param playersOnly    if the command is players only.
     * @return The CommandBase implementation
     */
    @NotNull
    CommandBase createSubcommand(@NotNull CommandBase parentDelegate,
                                 @NotNull EcoPlugin plugin,
                                 @NotNull String name,
                                 @NotNull String permission,
                                 boolean playersOnly);

    /**
     * Updatable config.
     *
     * @param configName            The name of the config
     * @param plugin                The plugin.
     * @param subDirectoryPath      The subdirectory path.
     * @param source                The class that owns the resource.
     * @param removeUnused          Whether keys not present in the default config should be removed on update.
     * @param type                  The config type.
     * @param updateBlacklist       Substring of keys to not add/remove keys for.
     * @param requiresChangesToSave If the config must be changed in order to save the config.
     * @return The config implementation.
     */
    @NotNull
    LoadableConfig createUpdatableConfig(@NotNull String configName,
                                         @NotNull PluginLike plugin,
                                         @NotNull String subDirectoryPath,
                                         @NotNull Class<?> source,
                                         boolean removeUnused,
                                         @NotNull ConfigType type,
                                         boolean requiresChangesToSave,
                                         @NotNull String... updateBlacklist);

    /**
     * Loadable config.
     *
     * @param configName            The name of the config
     * @param plugin                The plugin.
     * @param subDirectoryPath      The subdirectory path.
     * @param source                The class that owns the resource.
     * @param type                  The config type.
     * @param requiresChangesToSave If the config must be changed in order to save the config.
     * @return The config implementation.
     */
    @NotNull
    LoadableConfig createLoadableConfig(@NotNull String configName,
                                        @NotNull PluginLike plugin,
                                        @NotNull String subDirectoryPath,
                                        @NotNull Class<?> source,
                                        @NotNull ConfigType type,
                                        boolean requiresChangesToSave);

    /**
     * Create config.
     *
     * @param config The handle.
     * @return The config implementation.
     */
    @NotNull
    Config wrapConfigurationSection(@NotNull ConfigurationSection config);

    /**
     * Create config.
     *
     * @param values The values.
     * @param type   The config type.
     * @return The config implementation.
     */
    @NotNull
    Config createConfig(@NotNull Map<String, Object> values,
                        @NotNull ConfigType type);

    /**
     * Create config.
     *
     * @param contents The file contents.
     * @param type     The type.
     * @return The config implementation.
     */
    @NotNull
    Config createConfig(@NotNull String contents,
                        @NotNull ConfigType type);

    /**
     * Create a Drop Queue.
     *
     * @param player The player.
     * @return The drop queue.
     */
    @NotNull
    DropQueue createDropQueue(@NotNull Player player);

    /**
     * Create slot builder.
     *
     * @param provider The provider.
     * @return The builder.
     */
    @NotNull
    SlotBuilder createSlotBuilder(@NotNull SlotProvider provider);

    /**
     * Create menu builder.
     *
     * @param rows The amount of rows.
     * @param type The type.
     * @return The builder.
     */
    @NotNull
    MenuBuilder createMenuBuilder(int rows,
                                  @NotNull MenuType type);

    /**
     * Combine the state of two menus together.
     *
     * @param base       The base menu.
     * @param additional The additional state.
     * @return The menu.
     */
    @NotNull
    Menu blendMenuState(@NotNull Menu base,
                        @NotNull Menu additional);

    /**
     * Clean up ClassLoader (etc.) to allow PlugMan support.
     *
     * @param plugin The plugin to clean up.
     */
    void clean(@NotNull EcoPlugin plugin);

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
     * Register a persistent data key to be stored.
     *
     * @param key The key.
     */
    void registerPersistentKey(@NotNull PersistentDataKey<?> key);

    /**
     * Get all registered keys.
     *
     * @return The keys.
     */
    @NotNull
    Set<PersistentDataKey<?>> getRegisteredPersistentDataKeys();

    /**
     * Load a player profile.
     *
     * @param uuid The UUID.
     * @return The profile.
     */
    @NotNull
    PlayerProfile loadPlayerProfile(@NotNull UUID uuid);

    /**
     * Load the server profile.
     *
     * @return The profile.
     */
    @NotNull
    ServerProfile getServerProfile();

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
     * Bypasses the constructor, allowing for the creation of invalid keys, therefore this is
     * considered unsafe and should only be called after the key has been confirmed to be valid.
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
     * Get item from SNBT.
     *
     * @param snbt The NBT string.
     * @return The ItemStack, or null if invalid.
     */
    @Nullable
    ItemStack fromSNBT(@NotNull String snbt);

    /**
     * Convert item to SNBT.
     *
     * @param itemStack The item.
     * @return The NBT string.
     */
    @NotNull
    String toSNBT(@NotNull ItemStack itemStack);

    /**
     * Make TestableItem from SNBT.
     *
     * @param snbt The NBT string.
     * @return The TestableItem.
     */
    @NotNull
    TestableItem testableItemFromSNBT(@NotNull String snbt);

    /**
     * Get the texture of a skull.
     *
     * @param meta The skull meta.
     * @return The texture, or null if not found.
     */
    @Nullable
    String getSkullTexture(@NotNull SkullMeta meta);

    /**
     * Set the texture of a skull.
     *
     * @param meta   The skull meta.
     * @param base64 The texture.
     */
    void setSkullTexture(@NotNull SkullMeta meta,
                         @NotNull String base64);

    /**
     * Get the current server TPS.
     *
     * @return The TPS.
     */
    double getTPS();

    /**
     * Evaluate an expression.
     *
     * @param expression The expression.
     * @param context    The context.
     * @return The value of the expression, or null if invalid.
     */
    @Nullable
    Double evaluate(@NotNull String expression,
                    @NotNull PlaceholderContext context);

    /**
     * Get the menu a player currently has open.
     *
     * @param player The player.
     * @return The menu, or null if no menu open.
     */
    @Nullable
    Menu getOpenMenu(@NotNull Player player);

    /**
     * Sync commands.
     */
    void syncCommands();

    /**
     * Unregister a command.
     *
     * @param command The command.
     */
    void unregisterCommand(@NotNull PluginCommandBase command);

    /**
     * Send a packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    void sendPacket(@NotNull Player player,
                    @NotNull Packet packet);

    /**
     * Translate placeholders in a string.
     *
     * @param text    The text.
     * @param context The context.
     * @return The translated text.
     */
    @NotNull
    String translatePlaceholders(@NotNull String text,
                                 @NotNull PlaceholderContext context);

    /**
     * Get the value of a placeholder.
     *
     * @param plugin  The plugin that owns the placeholder.
     * @param args    The placeholder arguments.
     * @param context The context.
     * @return The value, or null if invalid.
     */
    @Nullable
    String getPlaceholderValue(@Nullable EcoPlugin plugin,
                               @NotNull String args,
                               @NotNull PlaceholderContext context);

    /**
     * Get the instance of eco; the bridge between the api frontend and the implementation backend.
     *
     * @return The instance of eco.
     */
    @ApiStatus.Internal
    static Eco get() {
        return Instance.get();
    }

    /**
     * Manages the internal frontend -> backend communication.
     */
    @ApiStatus.Internal
    final class Instance {

        /**
         * Instance of eco.
         */
        @ApiStatus.Internal
        private static Eco eco;

        /**
         * Initialize eco.
         *
         * @param eco The instance of eco.
         */
        @ApiStatus.Internal
        static void set(@NotNull final Eco eco) {
            Validate.isTrue(Instance.eco == null, "Already initialized!");

            Instance.eco = eco;
        }

        /**
         * Get eco.
         *
         * @return eco.
         */
        static Eco get() {
            return eco;
        }

        private Instance() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }
}
