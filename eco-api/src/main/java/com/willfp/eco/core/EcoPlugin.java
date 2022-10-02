package com.willfp.eco.core;

import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.config.base.ConfigYml;
import com.willfp.eco.core.config.base.LangYml;
import com.willfp.eco.core.config.updating.ConfigHandler;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.events.EventManager;
import com.willfp.eco.core.extensions.Extension;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.factory.MetadataValueFactory;
import com.willfp.eco.core.factory.NamespacedKeyFactory;
import com.willfp.eco.core.factory.RunnableFactory;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.scheduling.Scheduler;
import com.willfp.eco.core.web.UpdateChecker;
import org.apache.commons.lang.Validate;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
public abstract class EcoPlugin extends JavaPlugin implements PluginLike {
    /**
     * The polymart resource ID of the plugin.
     */
    private final int resourceId;

    /**
     * The bStats resource ID of the plugin.
     */
    private final int bStatsId;

    /**
     * The package where proxy implementations are.
     */
    private final String proxyPackage;

    /**
     * The color of the plugin, used in messages.
     */
    private final String color;

    /**
     * Loaded integrations.
     */
    private final Set<String> loadedIntegrations = new HashSet<>();

    /**
     * The plugin scheduler.
     */
    private final Scheduler scheduler;

    /**
     * The plugin Event Manager.
     */
    private final EventManager eventManager;

    /**
     * Config.yml.
     */
    private final ConfigYml configYml;

    /**
     * Lang.yml.
     */
    private final LangYml langYml;

    /**
     * The factory to produce {@link org.bukkit.NamespacedKey}s.
     */
    private final NamespacedKeyFactory namespacedKeyFactory;

    /**
     * The factory to produce {@link org.bukkit.metadata.FixedMetadataValue}s.
     */
    private final MetadataValueFactory metadataValueFactory;

    /**
     * The factory to produce {@link com.willfp.eco.core.scheduling.RunnableTask}s.
     */
    private final RunnableFactory runnableFactory;

    /**
     * The loader for all plugin extensions.
     *
     * @see com.willfp.eco.core.extensions.Extension
     */
    private final ExtensionLoader extensionLoader;

    /**
     * The handler class for updatable classes.
     */
    private final ConfigHandler configHandler;

    /**
     * The display module for the plugin.
     */
    private DisplayModule displayModule;

    /**
     * The logger for the plugin.
     */
    private final Logger logger;

    /**
     * If the server is running an outdated version of the plugin.
     */
    private boolean outdated = false;

    /**
     * If the plugin supports extensions.
     */
    private final boolean supportingExtensions;

    /**
     * The proxy factory.
     */
    @Nullable
    private final ProxyFactory proxyFactory;

    /**
     * Create a new plugin.
     * <p>
     * Will read from eco.yml (like plugin.yml) to fetch values that would otherwise be passed
     * into the constructor. If no eco.yml is present, the plugin will load without extension
     * support, without proxy support, with no update-checker or bStats, and with the color white.
     */
    protected EcoPlugin() {
        this((PluginProps) null);
    }

    /**
     * Create a new plugin without proxy support, polymart, or bStats.
     *
     * @param color The color.
     */
    protected EcoPlugin(@NotNull final String color) {
        this("", color);
    }


    /**
     * Create a new plugin unlinked to polymart and bStats.
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
     * @param resourceId The polymart resource ID for the plugin.
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
     * @param resourceId           The polymart resource ID for the plugin.
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
     * @param resourceId   The polymart resource ID for the plugin.
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
     * @param resourceId           The polymart resource ID for the plugin.
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
        this(
                PluginProps.createSimple(
                        resourceId,
                        bStatsId,
                        proxyPackage,
                        color,
                        supportingExtensions
                )
        );
    }

    /**
     * Create a new plugin.
     *
     * @param pluginProps The props. If left null, it will read from eco.yml.
     */
    protected EcoPlugin(@Nullable final PluginProps pluginProps) {
        /*
        Eco must be initialized before any plugin's constructors
        are called, as the constructors call Eco#get().

        To fix this, EcoSpigotPlugin an abstract class and the 'actual'
        plugin class is EcoImpl - that way I can initialize eco
        before any plugins are loaded while still having a separation between
        the plugin class and the implementation class (for code clarity).

        I don't really like the fact that the implementation class *is* the
        spigot plugin, but it is what it is.

        There is probably a better way of doing it - maybe with
        some sort of EcoCrater interface in order to still have
        a standalone eco class, but then there would be an interface
        left in the API that doesn't really help anything.

        The other alternative would be to use reflection to get a 'createEco'
        method that only exists in EcoSpigotPlugin - but that feels filthy,
        and I'd rather only use reflection where necessary.
        */

        if (Eco.get() == null && this instanceof Eco) {
            /*
            This code is only ever called by EcoSpigotPlugin (EcoImpl)
            as it's the first plugin to load, and it's an instance of eco.

            Any other plugins will never call this code as eco will have already
            been initialized.
             */

            Eco.Instance.set((Eco) this);
        }

        assert Eco.get() != null;

        PluginProps generatedProps = Eco.get().getProps(pluginProps, this.getClass());
        generatedProps.validate();
        PluginProps props = this.mutateProps(generatedProps);
        props.validate();

        this.resourceId = props.getResourceId();
        this.bStatsId = props.getBStatsId();
        this.proxyPackage = props.getProxyPackage();
        this.color = props.getColor();
        this.supportingExtensions = props.isSupportingExtensions();

        this.proxyFactory = this.proxyPackage.equalsIgnoreCase("") ? null : Eco.get().createProxyFactory(this);
        this.logger = Eco.get().createLogger(this);

        this.getLogger().info("Initializing " + this.getColor() + this.getName());

        this.scheduler = Eco.get().createScheduler(this);
        this.eventManager = Eco.get().createEventManager(this);
        this.namespacedKeyFactory = Eco.get().createNamespacedKeyFactory(this);
        this.metadataValueFactory = Eco.get().createMetadataValueFactory(this);
        this.runnableFactory = Eco.get().createRunnableFactory(this);
        this.extensionLoader = Eco.get().createExtensionLoader(this);
        this.configHandler = Eco.get().createConfigHandler(this);

        this.langYml = this.createLangYml();
        this.configYml = this.createConfigYml();

        Eco.get().addNewPlugin(this);

        /*
        The minimum eco version check was moved here because it's very common
        to add a lot of code in the constructor of plugins; meaning that the plugin
        can throw errors without it being obvious to the user that the reason is that
        they have an outdated version of eco installed.
         */

        DefaultArtifactVersion runningVersion = new DefaultArtifactVersion(Eco.get().getEcoPlugin().getDescription().getVersion());
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

        if (this.getResourceId() != 0 && !Eco.get().getEcoPlugin().getConfigYml().getBool("no-update-checker")) {
            new UpdateChecker(this).getVersion(version -> {
                DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(this.getDescription().getVersion());
                DefaultArtifactVersion mostRecentVersion = new DefaultArtifactVersion(version);
                if (!(currentVersion.compareTo(mostRecentVersion) > 0 || currentVersion.equals(mostRecentVersion))) {
                    this.outdated = true;
                    this.getLogger().warning(this.getName() + " is out of date! (Version " + this.getDescription().getVersion() + ")");
                    this.getLogger().warning("The newest version is " + version);
                    this.getLogger().warning("Download the new version!");
                }
            });
        }

        if (this.getBStatsId() != 0) {
            Eco.get().registerBStats(this);
        }

        Set<String> enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .map(Plugin::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (enabledPlugins.contains("PlaceholderAPI".toLowerCase())) {
            this.loadedIntegrations.add("PlaceholderAPI");
            PlaceholderManager.addIntegration(Eco.get().createPAPIIntegration(this));
        }

        this.loadIntegrationLoaders().forEach(integrationLoader -> {
            if (enabledPlugins.contains(integrationLoader.getPluginName().toLowerCase())) {
                this.loadedIntegrations.add(integrationLoader.getPluginName());
                integrationLoader.load();
            }
        });

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
        Eco.get().clean(this);
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

        for (Extension extension : this.getExtensionLoader().getLoadedExtensions()) {
            extension.handleAfterLoad();
        }

        this.getLogger().info("Loaded " + this.color + this.getName());
    }

    /**
     * Reload the plugin.
     */
    public final void reload() {
        this.getConfigHandler().updateConfigs();

        this.getScheduler().cancelAll();
        this.getConfigHandler().callUpdate();
        this.getConfigHandler().callUpdate(); // Call twice to fix issues

        this.handleReload();

        for (Extension extension : this.extensionLoader.getLoadedExtensions()) {
            extension.handleReload();
        }
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
     * Mutate the plugin props.
     * <p>
     * Useful for eco-based plugin libraries to enforce certain properties, such as
     * forcing extensions to be enabled.
     * <p>
     * Props are validated both before and after calling this method.
     *
     * @param props The props.
     * @return The mutated props.
     */
    protected PluginProps mutateProps(@NotNull final PluginProps props) {
        return props;
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
    public final <T> T getProxy(@NotNull final Class<T> proxyClass) {
        Validate.notNull(proxyFactory, "Plugin does not support proxies!");

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

        return Objects.requireNonNull(this.getConfigYml().toBukkit());
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
        return Eco.get().getPluginByName(pluginName);
    }

    /**
     * Get all EcoPlugin names.
     *
     * @return The set of names.
     */
    public static Set<String> getPluginNames() {
        return new HashSet<>(Eco.get().getLoadedPlugins());
    }

    /**
     * Get the polymart resource ID.
     *
     * @return The resource ID.
     */
    public int getResourceId() {
        return this.resourceId;
    }

    /**
     * Get the bStats ID.
     *
     * @return The ID.
     */
    public int getBStatsId() {
        return this.bStatsId;
    }

    /**
     * Get the proxy package.
     *
     * @return The package where proxies are contained.
     */
    public String getProxyPackage() {
        return this.proxyPackage;
    }

    /**
     * Get the plugin color (uses legacy formatting).
     *
     * @return The color.
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Get all loaded integration names.
     *
     * @return The integrations.
     */
    public Set<String> getLoadedIntegrations() {
        return this.loadedIntegrations;
    }

    /**
     * Get the scheduler.
     *
     * @return The scheduler.
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Get the event manager.
     *
     * @return The event manager.
     */
    public EventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * Get config.yml.
     *
     * @return config.yml.
     */
    public ConfigYml getConfigYml() {
        return this.configYml;
    }

    /**
     * Get lang.yml.
     *
     * @return lang.yml.
     */
    public LangYml getLangYml() {
        return this.langYml;
    }

    /**
     * Get the NamespacedKey factory.
     *
     * @return The factory.
     */
    public NamespacedKeyFactory getNamespacedKeyFactory() {
        return this.namespacedKeyFactory;
    }

    /**
     * Get the metadata value factory.
     *
     * @return The factory.
     */
    public MetadataValueFactory getMetadataValueFactory() {
        return this.metadataValueFactory;
    }

    /**
     * Get the runnable factory.
     *
     * @return The runnable factory.
     */
    public RunnableFactory getRunnableFactory() {
        return this.runnableFactory;
    }

    /**
     * Get the extension loader.
     *
     * @return The extension loader.
     */
    public ExtensionLoader getExtensionLoader() {
        return this.extensionLoader;
    }

    /**
     * Get the config handler.
     *
     * @return The config handler.
     */
    public ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    /**
     * Get the plugin's display module.
     *
     * @return The display module.
     */
    @Nullable
    public DisplayModule getDisplayModule() {
        return this.displayModule;
    }

    /**
     * Get if the plugin is outdated.
     *
     * @return If outdated.
     */
    public boolean isOutdated() {
        return this.outdated;
    }

    /**
     * Get if the plugin supports extensions.
     *
     * @return If extensions are supported.
     */
    public boolean isSupportingExtensions() {
        return this.supportingExtensions;
    }

    /**
     * Get the proxy factory.
     *
     * @return The proxy factory.
     */
    @Nullable
    public ProxyFactory getProxyFactory() {
        return this.proxyFactory;
    }

    /**
     * Create a NamespacedKey.
     *
     * @param key The key.
     * @return The namespaced key.
     */
    @NotNull
    public NamespacedKey createNamespacedKey(@NotNull final String key) {
        return this.getNamespacedKeyFactory().create(key);
    }

    /**
     * Create a metadata value.
     *
     * @param value The value.
     * @return The metadata value.
     */
    @NotNull
    public FixedMetadataValue createMetadataValue(@NotNull final Object value) {
        return this.getMetadataValueFactory().create(value);
    }
}
