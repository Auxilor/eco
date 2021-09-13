package com.willfp.eco.core;

import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.config.base.ConfigYml;
import com.willfp.eco.core.config.base.LangYml;
import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.proxy.AbstractProxy;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.scheduling.Scheduler;
import com.willfp.eco.core.web.UpdateChecker;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * EcoPlugin is the base plugin class for eco-based plugins.
 * <p>
 * It functions as a replacement for {@link JavaPlugin}.
 * <p>
 * EcoPlugin is a lot more powerful than {@link JavaPlugin} and
 * contains many methods to reduce boilerplate code and reduce
 * plugin complexity.
 * <p>
 * It is recommended to view the source code for this class to
 * gain a better understanding of how it works.
 * <p>
 * <b>IMPORTANT: When reloading a plugin, all runnables / tasks will
 * be cancelled.</b>
 */
@SuppressWarnings("unused")
public abstract class EcoPlugin extends JavaPlugin {
    /**
     * The spigot resource ID of the plugin.
     */
    @Getter
    private final int resourceId;

    /**
     * The bStats resource ID of the plugin.
     */
    @Getter
    private final int bStatsId;

    /**
     * The package where proxy implementations are.
     */
    @Getter
    private final String proxyPackage;

    /**
     * The color of the plugin, used in messages.
     */
    @Getter
    private final String color;

    /**
     * Loaded integrations.
     */
    @Getter
    private final Set<String> loadedIntegrations = new HashSet<>();

    /**
     * The internal plugin scheduler.
     */
    @Getter
    private final Scheduler scheduler;

    /**
     * The internal plugin Event Manager.
     */
    @Getter
    private final EventManager eventManager;

    /**
     * Config.yml.
     */
    @Getter
    private final ConfigYml configYml;

    /**
     * Lang.yml.
     */
    @Getter
    private final LangYml langYml;

    /**
     * The internal factory to produce {@link org.bukkit.NamespacedKey}s.
     */
    @Getter
    private final NamespacedKeyFactory namespacedKeyFactory;

    /**
     * The internal factory to produce {@link org.bukkit.metadata.FixedMetadataValue}s.
     */
    @Getter
    private final MetadataValueFactory metadataValueFactory;

    /**
     * The internal factory to produce {@link com.willfp.eco.core.scheduling.RunnableTask}s.
     */
    @Getter
    private final RunnableFactory runnableFactory;

    /**
     * The loader for all plugin extensions.
     *
     * @see com.willfp.eco.core.extensions.Extension
     */
    @Getter
    private final ExtensionLoader extensionLoader;

    /**
     * The handler class for updatable classes.
     */
    @Getter
    private final ConfigHandler configHandler;

    /**
     * The display module for the plugin.
     */
    @Getter
    private DisplayModule displayModule;

    /**
     * The logger for the plugin.
     */
    private final Logger logger;

    /**
     * If the server is running an outdated version of the plugin.
     */
    @Getter
    private boolean outdated = false;

    /**
     * If the plugin supports extensions.
     */
    @Getter
    private final boolean supportingExtensions;

    /**
     * The proxy factory.
     */
    @Getter
    private final ProxyFactory proxyFactory;

    /**
     * Create a new plugin without a specified color, proxy support, spigot, or bStats.
     */
    protected EcoPlugin() {
        this("&f");
    }

    /**
     * Create a new plugin without proxy support, spigot, or bStats.
     *
     * @param color The color.
     */
    protected EcoPlugin(@NotNull final String color) {
        this("", color);
    }


    /**
     * Create a new plugin unlinked to spigot and bStats.
     *
     * @param proxyPackage The package where proxy implementations are stored.
     * @param color        The color of the plugin (used in messages, using standard formatting)
     */
    protected EcoPlugin(@NotNull final String proxyPackage,
                        @NotNull final String color) {
        this(0, 0, proxyPackage, color);
    }

    /**
     * Create a new plugin without proxy or extension support.
     *
     * @param resourceId The spigot resource ID for the plugin.
     * @param bStatsId   The bStats resource ID for the plugin.
     * @param color      The color of the plugin (used in messages, using standard formatting)
     */
    protected EcoPlugin(final int resourceId,
                        final int bStatsId,
                        @NotNull final String color) {
        this(resourceId, bStatsId, "", color);
    }

    /**
     * Create a new plugin without proxy support.
     *
     * @param resourceId           The spigot resource ID for the plugin.
     * @param bStatsId             The bStats resource ID for the plugin.
     * @param color                The color of the plugin (used in messages, using standard formatting)
     * @param supportingExtensions If the plugin supports extensions.
     */
    protected EcoPlugin(final int resourceId,
                        final int bStatsId,
                        @NotNull final String color,
                        final boolean supportingExtensions) {
        this(resourceId, bStatsId, "", color, supportingExtensions);
    }

    /**
     * Create a new plugin without extension support.
     *
     * @param resourceId   The spigot resource ID for the plugin.
     * @param bStatsId     The bStats resource ID for the plugin.
     * @param proxyPackage The package where proxy implementations are stored.
     * @param color        The color of the plugin (used in messages, using standard formatting)
     */
    protected EcoPlugin(final int resourceId,
                        final int bStatsId,
                        @NotNull final String proxyPackage,
                        @NotNull final String color) {
        this(resourceId, bStatsId, proxyPackage, color, false);
    }

    /**
     * Create a new plugin.
     *
     * @param resourceId           The spigot resource ID for the plugin.
     * @param bStatsId             The bStats resource ID for the plugin.
     * @param proxyPackage         The package where proxy implementations are stored.
     * @param color                The color of the plugin (used in messages, using standard formatting)
     * @param supportingExtensions If the plugin supports extensions.
     */
    protected EcoPlugin(final int resourceId,
                        final int bStatsId,
                        @NotNull final String proxyPackage,
                        @NotNull final String color,
                        final boolean supportingExtensions) {
        /*
        The handler must be initialized before any plugin's constructors
        are called, as the constructors call Eco#getHandler().

        To fix this, EcoSpigotPlugin an abstract class and the 'actual'
        plugin class is EcoHandler - that way I can create the handler
        before any plugins are loaded while still having a separation between
        the plugin class and the handler class (for code clarity).

        I don't really like the fact that the handler class *is* the
        spigot plugin, but it is what it is.

        There is probably a better way of doing it - maybe with
        some sort of HandlerCreator interface in order to still have
        a standalone handler class, but then there would be an interface
        left in the API that doesn't really help anything.

        The other alternative would be do use reflection to get a 'createHandler'
        method that only exists in EcoSpigotPlugin - but that feels really dirty
        and I'd rather only use reflection where necessary.
        */

        if (Eco.getHandler() == null && this instanceof Handler) {
            /*
            This code is only ever called by EcoSpigotPlugin (EcoHandler)
            as it's the first plugin to load and it is a handler.

            Any other plugins will never call this code as the handler
            will have already been initialized.
             */

            Eco.setHandler((Handler) this);
        }

        assert Eco.getHandler() != null;

        this.resourceId = resourceId;
        this.bStatsId = bStatsId;
        this.proxyPackage = proxyPackage;
        this.color = color;
        this.supportingExtensions = supportingExtensions;

        this.scheduler = Eco.getHandler().createScheduler(this);
        this.eventManager = Eco.getHandler().createEventManager(this);
        this.namespacedKeyFactory = Eco.getHandler().createNamespacedKeyFactory(this);
        this.metadataValueFactory = Eco.getHandler().createMetadataValueFactory(this);
        this.runnableFactory = Eco.getHandler().createRunnableFactory(this);
        this.extensionLoader = Eco.getHandler().createExtensionLoader(this);
        this.configHandler = Eco.getHandler().createConfigHandler(this);
        this.logger = Eco.getHandler().createLogger(this);
        this.proxyFactory = this.proxyPackage.equalsIgnoreCase("") ? null : Eco.getHandler().createProxyFactory(this);

        this.langYml = this.createLangYml();
        this.configYml = this.createConfigYml();

        Eco.getHandler().addNewPlugin(this);

        /*
        The minimum eco version check was moved here because it's very common
        to add a lot of code in the constructor of plugins; meaning that the plugin
        can throw errors without it being obvious to the user that the reason is
        because they have an outdated version of eco installed.
         */

        DefaultArtifactVersion runningVersion = new DefaultArtifactVersion(Eco.getHandler().getEcoPlugin().getDescription().getVersion());
        DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(this.getMinimumEcoVersion());
        if (!(runningVersion.compareTo(requiredVersion) > 0 || runningVersion.equals(requiredVersion))) {
            this.getLogger().severe("You are running an outdated version of eco!");
            this.getLogger().severe("You must be on at least" + this.getMinimumEcoVersion());
            this.getLogger().severe("Download the newest version here:");
            this.getLogger().severe("https://polymart.org/download/773/recent/JSpprMspkuyecf5y1wQ2Jn8OoLQSQ_IW");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Default code to be executed on plugin enable.
     */
    @Override
    public final void onEnable() {
        super.onEnable();

        this.getLogger().info("");
        this.getLogger().info("Loading " + this.getColor() + this.getName());

        if (this.getResourceId() != 0) {
            new UpdateChecker(this).getVersion(version -> {
                DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(this.getDescription().getVersion());
                DefaultArtifactVersion mostRecentVersion = new DefaultArtifactVersion(version);
                if (!(currentVersion.compareTo(mostRecentVersion) > 0 || currentVersion.equals(mostRecentVersion))) {
                    this.outdated = true;
                    this.getScheduler().runTimer(() -> {
                        this.getLogger().info("&c " + this.getName() + " is out of date! (Version " + this.getDescription().getVersion() + ")");
                        this.getLogger().info("&cThe newest version is &f" + version);
                        this.getLogger().info("&cDownload the new version!");
                    }, 0, 864000);
                }
            });
        }

        if (this.getBStatsId() != 0) {
            Eco.getHandler().registerBStats(this);
        }

        Set<String> enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (enabledPlugins.contains("PlaceholderAPI".toLowerCase())) {
            this.loadedIntegrations.add("PlaceholderAPI");
            PlaceholderManager.addIntegration(Eco.getHandler().createPAPIIntegration(this));
        }

        this.loadIntegrationLoaders().forEach((integrationLoader -> {
            if (enabledPlugins.contains(integrationLoader.getPluginName().toLowerCase())) {
                this.loadedIntegrations.add(integrationLoader.getPluginName());
                integrationLoader.load();
            }
        }));

        this.getLogger().info("Loaded integrations: " + String.join(", ", this.getLoadedIntegrations()));

        Prerequisite.update();

        this.loadPacketAdapters().forEach(abstractPacketAdapter -> {
            if (!abstractPacketAdapter.isPostLoad()) {
                abstractPacketAdapter.register();
            }
        });

        this.loadListeners().forEach(listener -> this.getEventManager().registerListener(listener));

        this.loadPluginCommands().forEach(PluginCommand::register);

        this.getScheduler().runLater(this::afterLoad, 1);

        if (this.isSupportingExtensions()) {
            this.getExtensionLoader().loadExtensions();

            if (this.getExtensionLoader().getLoadedExtensions().isEmpty()) {
                this.getLogger().info("&cNo extensions found");
            } else {
                this.getLogger().info("Extensions Loaded:");
                this.getExtensionLoader().getLoadedExtensions().forEach(extension -> this.getLogger().info("- " + extension.getName() + " v" + extension.getVersion()));
            }
        }

        this.handleEnable();

        this.getLogger().info("");
    }

    /**
     * Default code to be executed on plugin disable.
     */
    @Override
    public final void onDisable() {
        super.onDisable();

        this.getEventManager().unregisterAllListeners();
        this.getScheduler().cancelAll();

        this.handleDisable();

        if (this.isSupportingExtensions()) {
            this.getExtensionLoader().unloadExtensions();
        }

        this.getLogger().info("Cleaning up...");
        Eco.getHandler().getCleaner().clean(this);
    }

    /**
     * Default code to be executed on plugin load.
     */
    @Override
    public final void onLoad() {
        super.onLoad();

        this.handleLoad();
    }

    /**
     * Default code to be executed after the server is up.
     */
    public final void afterLoad() {
        this.displayModule = createDisplayModule();

        if (this.getDisplayModule() != null) {
            Display.registerDisplayModule(this.getDisplayModule());
        }

        this.loadPacketAdapters().forEach(abstractPacketAdapter -> {
            if (abstractPacketAdapter.isPostLoad()) {
                abstractPacketAdapter.register();
            }
        });

        if (!Prerequisite.HAS_PAPER.isMet()) {
            this.getLogger().severe("");
            this.getLogger().severe("----------------------------");
            this.getLogger().severe("");
            this.getLogger().severe("You don't seem to be running paper!");
            this.getLogger().severe("Paper is strongly recommended for all servers,");
            this.getLogger().severe("and some things may not function properly without it");
            this.getLogger().severe("Download Paper from &fhttps://papermc.io");
            this.getLogger().severe("");
            this.getLogger().severe("----------------------------");
            this.getLogger().severe("");
        }

        this.handleAfterLoad();

        this.reload();

        this.getLogger().info("Loaded " + this.color + this.getName());
    }

    /**
     * Reload the plugin.
     */
    public final void reload() {
        this.getConfigHandler().updateConfigs();

        this.getConfigHandler().callUpdate();
        this.getConfigHandler().callUpdate(); // Call twice to fix issues
        this.getScheduler().cancelAll();

        this.handleReload();
    }

    /**
     * Reload the plugin and return the time taken to reload.
     *
     * @return The time.
     */
    public final long reloadWithTime() {
        long startTime = System.currentTimeMillis();

        this.reload();

        return System.currentTimeMillis() - startTime;
    }

    /**
     * The plugin-specific code to be executed on enable.
     * <p>
     * Override when needed.
     */
    protected void handleEnable() {

    }

    /**
     * The plugin-specific code to be executed on disable.
     * <p>
     * Override when needed.
     */
    protected void handleDisable() {

    }

    /**
     * The plugin-specific code to be executed on load.
     * <p>
     * This is executed before enabling.
     * <p>
     * Override when needed.
     */
    protected void handleLoad() {

    }

    /**
     * The plugin-specific code to be executed on reload.
     * <p>
     * Override when needed.
     */
    protected void handleReload() {

    }

    /**
     * The plugin-specific code to be executed after the server is up.
     * <p>
     * Override when needed.
     */
    protected void handleAfterLoad() {

    }

    /**
     * The plugin-specific integrations to be tested and loaded.
     *
     * @return A list of integrations.
     */
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return new ArrayList<>();
    }

    /**
     * The commands to be registered.
     *
     * @return A list of commands.
     */
    protected List<PluginCommand> loadPluginCommands() {
        return new ArrayList<>();
    }

    /**
     * ProtocolLib packet adapters to be registered.
     * <p>
     * If the plugin does not require ProtocolLib this can be left empty.
     *
     * @return A list of packet adapters.
     */
    protected List<AbstractPacketAdapter> loadPacketAdapters() {
        return new ArrayList<>();
    }

    /**
     * All listeners to be registered.
     *
     * @return A list of all listeners.
     */
    protected abstract List<Listener> loadListeners();

    /**
     * Useful for custom LangYml implementations.
     * <p>
     * Override if needed.
     *
     * @return lang.yml.
     */
    protected LangYml createLangYml() {
        return new LangYml(this);
    }

    /**
     * Useful for custom ConfigYml implementations.
     * <p>
     * Override if needed.
     *
     * @return config.yml.
     */
    protected ConfigYml createConfigYml() {
        return new ConfigYml(this);
    }

    /**
     * Create the display module for the plugin.
     *
     * @return The display module, or null.
     */
    @Nullable
    protected DisplayModule createDisplayModule() {
        Validate.isTrue(
                this.getDisplayModule() == null,
                "Display module exists!"
        );

        return null;
    }

    /**
     * Get the minimum version of eco to use the plugin.
     *
     * @return The version.
     */
    public String getMinimumEcoVersion() {
        return "6.0.0";
    }

    /**
     * Get the plugin's logger.
     *
     * @return The logger.
     */
    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Get a proxy.
     *
     * @param proxyClass The proxy class.
     * @param <T>        The proxy type.
     * @return The proxy.
     */
    public final <T extends AbstractProxy> T getProxy(@NotNull final Class<T> proxyClass) {
        Validate.notNull(proxyFactory, "Plugin does not support proxy!");

        return proxyFactory.getProxy(proxyClass);
    }

    /**
     * Get unwrapped config.
     * Does not use eco config system, don't use.
     *
     * @return The bukkit config.
     * @deprecated Use getConfigYml() instead.
     */
    @NotNull
    @Override
    @Deprecated
    public final FileConfiguration getConfig() {
        this.getLogger().warning("Call to default config method in eco plugin!");

        return this.getConfigYml().getBukkitHandle();
    }

    /**
     * Does not use eco config system, don't use.
     *
     * @deprecated Use eco config system.
     */
    @Override
    @Deprecated
    public final void saveConfig() {
        this.getLogger().warning("Call to default config method in eco plugin!");

        super.saveConfig();
    }

    /**
     * Does not use eco config system, don't use.
     *
     * @deprecated Use eco config system.
     */
    @Override
    @Deprecated
    public final void saveDefaultConfig() {
        this.getLogger().warning("Call to default config method in eco plugin!");

        super.saveDefaultConfig();
    }

    /**
     * Does not use eco config system, don't use.
     *
     * @deprecated Use eco config system.
     */
    @Override
    @Deprecated
    public final void reloadConfig() {
        this.getLogger().warning("Call to default config method in eco plugin!");

        super.reloadConfig();
    }

    /**
     * Get an EcoPlugin by name.
     *
     * @param pluginName The name.
     * @return The plugin.
     */
    public static EcoPlugin getPlugin(@NotNull final String pluginName) {
        return Eco.getHandler().getPluginByName(pluginName);
    }

    /**
     * Get all EcoPlugin names.
     *
     * @return The set of names.
     */
    public static Set<String> getPluginNames() {
        return new HashSet<>(Eco.getHandler().getLoadedPlugins());
    }
}
