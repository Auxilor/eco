package com.willfp.eco.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
import com.willfp.eco.core.map.ListMap;
import com.willfp.eco.core.packet.PacketListener;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.registry.Registrable;
import com.willfp.eco.core.registry.Registry;
import com.willfp.eco.core.scheduling.Scheduler;
import com.willfp.eco.core.version.OutdatedEcoVersionError;
import com.willfp.eco.core.version.Version;
import com.willfp.eco.core.web.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
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
@SuppressWarnings({"unused", "DeprecatedIsStillUsed", "MismatchedQueryAndUpdateOfCollection"})
public abstract class EcoPlugin extends JavaPlugin implements PluginLike, Registrable {
    /**
     * The properties (eco.yml).
     */
    private final PluginProps props;

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
     * The factory to produce {@link NamespacedKey}s.
     */
    private final NamespacedKeyFactory namespacedKeyFactory;

    /**
     * The factory to produce {@link FixedMetadataValue}s.
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
     *
     * @deprecated Plugins can now have multiple display modules.
     */
    @Deprecated(since = "6.72.0")
    private DisplayModule displayModule;

    /**
     * The display modules for the plugin.
     */
    private List<DisplayModule> displayModules = new ArrayList<>();

    /**
     * The logger for the plugin.
     */
    private Logger logger;

    /**
     * If the server is running an outdated version of the plugin.
     */
    private boolean outdated = false;

    /**
     * The proxy factory.
     */
    @Nullable
    private final ProxyFactory proxyFactory;

    /**
     * The tasks to run on enable.
     */
    private final ListMap<LifecyclePosition, Runnable> onEnable = new ListMap<>();

    /**
     * The tasks to run on disable.
     */
    private final ListMap<LifecyclePosition, Runnable> onDisable = new ListMap<>();

    /**
     * The tasks to run on reload.
     */
    private final ListMap<LifecyclePosition, Runnable> onReload = new ListMap<>();

    /**
     * The tasks to run on load.
     */
    private final ListMap<LifecyclePosition, Runnable> onLoad = new ListMap<>();

    /**
     * The tasks to run after load.
     */
    private final ListMap<LifecyclePosition, Runnable> afterLoad = new ListMap<>();

    /**
     * The tasks to run on task creation.
     */
    private final ListMap<LifecyclePosition, Runnable> onCreateTasks = new ListMap<>();

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
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    protected EcoPlugin(@NotNull final String color) {
        this("", color);
    }


    /**
     * Create a new plugin unlinked to polymart and bStats.
     *
     * @param proxyPackage The package where proxy implementations are stored.
     * @param color        The color of the plugin (used in messages, using standard formatting)
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
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
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
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
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
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
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
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
     * @deprecated Use eco.yml instead.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    protected EcoPlugin(final int resourceId,
                        final int bStatsId,
                        @NotNull final String proxyPackage,
                        @NotNull final String color,
                        final boolean supportingExtensions) {
        this(PluginProps.createSimple(resourceId, bStatsId, proxyPackage, color, supportingExtensions));
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

        this.props = props;

        this.proxyFactory = this.props.getProxyPackage().equalsIgnoreCase("") ? null : Eco.get().createProxyFactory(this);
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

        if (!this.langYml.isValid() && !(this instanceof Eco)) {
            this.getLogger().warning("Notify plugin authors " + String.join(", ", this.getDescription().getAuthors()) + " that");
            this.getLogger().warning("they are missing crucial lang.yml keys! They can be found");
            this.getLogger().warning("in the LangYml class.");
        }

        this.configYml = this.createConfigYml();

        Eco.get().addNewPlugin(this);

        /*
        The minimum eco version check was moved here because it's very common
        to add a lot of code in the constructor of plugins; meaning that the plugin
        can throw errors without it being obvious to the user that the reason is that
        they have an outdated version of eco installed.
         */

        Version runningVersion = new Version(Eco.get().getEcoPlugin().getDescription().getVersion());

        // Support for both legacy and new props configuration
        Version requiredVersion = new Version(this.getMinimumEcoVersion());
        if (this.getProps().getEcoApiVersion().compareTo(requiredVersion) > 0) {
            requiredVersion = this.getProps().getEcoApiVersion();
        }

        if (!(runningVersion.compareTo(requiredVersion) > 0 || runningVersion.equals(requiredVersion))) {
            this.getLogger().severe("You are running an outdated version of eco!");
            this.getLogger().severe("You must be on at least" + requiredVersion);
            this.getLogger().severe("Download the newest version here:");
            this.getLogger().severe("https://polymart.org/product/773/eco");
            throw new OutdatedEcoVersionError("This plugin requires at least eco version " + requiredVersion + " to run.");
        }
    }

    /**
     * Default code to be executed on plugin enable.
     */
    @Override
    public final void onEnable() {
        super.onEnable();

        this.getLogger().info("Loading " + this.getColor() + this.getName());

        if (this.getResourceId() != 0 && !Eco.get().getEcoPlugin().getConfigYml().getBool("no-update-checker")) {
            new UpdateChecker(this).getVersion(version -> {
                Version currentVersion = new Version(this.getDescription().getVersion());
                Version mostRecentVersion = new Version(version);
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

        Set<String> enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).map(String::toLowerCase).collect(Collectors.toSet());

        if (enabledPlugins.contains("PlaceholderAPI".toLowerCase())) {
            Eco.get().createPAPIIntegration(this);
        }

        this.loadIntegrationLoaders().forEach(integrationLoader -> {
            if (enabledPlugins.contains(integrationLoader.getPluginName().toLowerCase())) {
                try {
                    integrationLoader.load();
                    this.loadedIntegrations.add(integrationLoader.getPluginName());
                } catch (Exception e) {
                    this.getLogger().warning("Failed to load integration for " + integrationLoader.getPluginName());
                    e.printStackTrace();
                }
            }
        });

        this.loadedIntegrations.removeIf(pl -> pl.equalsIgnoreCase(this.getName()));

        if (!this.getLoadedIntegrations().isEmpty()) {
            this.getLogger().info("Loaded integrations: " + String.join(", ", this.getLoadedIntegrations()));
        }

        Prerequisite.update();

        this.loadListeners().forEach(listener -> this.getEventManager().registerListener(listener));
        this.loadPacketListeners().forEach(listener -> this.getEventManager().registerPacketListener(listener));

        this.loadPluginCommands().forEach(PluginCommand::register);

        // Run preliminary reload to resolve load order issues
        this.getScheduler().runLater(() -> {
            Logger before = this.getLogger();
            // Temporary silence logger.
            //this.logger = Eco.get().getNOOPLogger();

            this.reload(false);

            //this.logger = before;
        }, 1);

        this.getScheduler().runLater(this::afterLoad, 2);

        if (this.isSupportingExtensions()) {
            this.getExtensionLoader().loadExtensions();

            if (!this.getExtensionLoader().getLoadedExtensions().isEmpty()) {
                List<String> loadedExtensions = this.getExtensionLoader().getLoadedExtensions().stream().map(
                        extension -> extension.getName() + " v" + extension.getVersion()
                ).toList();

                this.getLogger().info(
                        "Loaded extensions: " +
                                String.join(", ", loadedExtensions)
                );
            }
        }

        this.handleLifecycle(this.onEnable, this::handleEnable);
    }

    /**
     * Add new task to run on enable.
     *
     * @param task The task.
     */
    public final void onEnable(@NotNull final Runnable task) {
        this.onEnable(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run on enable.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void onEnable(@NotNull final LifecyclePosition position,
                               @NotNull final Runnable task) {
        this.onEnable.append(position, task);
    }

    /**
     * Default code to be executed on plugin disable.
     */
    @Override
    public final void onDisable() {
        super.onDisable();

        this.getEventManager().unregisterAllListeners();
        this.getScheduler().cancelAll();

        this.handleLifecycle(this.onDisable, this::handleDisable);

        if (this.isSupportingExtensions()) {
            this.getExtensionLoader().unloadExtensions();
        }

        this.getLogger().info("Cleaning up...");
        Eco.get().clean(this);
    }

    /**
     * Add new task to run on disable.
     *
     * @param task The task.
     */
    public final void onDisable(@NotNull final Runnable task) {
        this.onDisable(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run on disable.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void onDisable(@NotNull final LifecyclePosition position,
                                @NotNull final Runnable task) {
        this.onDisable.append(position, task);
    }

    /**
     * Default code to be executed on plugin load.
     */
    @Override
    public final void onLoad() {
        super.onLoad();

        this.handleLifecycle(this.onLoad, this::handleLoad);
    }

    /**
     * Add new task to run on load.
     *
     * @param task The task.
     */
    public final void onLoad(@NotNull final Runnable task) {
        this.onLoad(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run on load.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void onLoad(@NotNull final LifecyclePosition position,
                             @NotNull final Runnable task) {
        this.onLoad.append(position, task);
    }

    /**
     * Default code to be executed after the server is up.
     */
    public final void afterLoad() {
        DisplayModule module = createDisplayModule();
        if (module != null) {
            Display.registerDisplayModule(module);
            this.displayModules.add(module);
        }

        for (DisplayModule displayModule : this.loadDisplayModules()) {
            Display.registerDisplayModule(displayModule);
            this.displayModules.add(displayModule);
        }

        if (!Prerequisite.HAS_PAPER.isMet()) {
            this.getLogger().severe("");
            this.getLogger().severe("----------------------------");
            this.getLogger().severe("");
            this.getLogger().severe("You don't seem to be running paper!");
            this.getLogger().severe("Paper is strongly recommended for all servers,");
            this.getLogger().severe("and many features may not function properly without it");
            this.getLogger().severe("Download Paper from https://papermc.io");
            this.getLogger().severe("It's a drop-in replacement for Spigot, so it's easy to switch.");
            this.getLogger().severe("");
            this.getLogger().severe("----------------------------");
            this.getLogger().severe("");
        }

        this.handleLifecycle(this.afterLoad, this::handleAfterLoad);

        this.reload();

        for (Extension extension : this.getExtensionLoader().getLoadedExtensions()) {
            extension.handleAfterLoad();
        }

        this.getLogger().info("Loaded " + this.props.getColor() + this.getName());
    }

    /**
     * Add new task to run after load.
     *
     * @param task The task.
     */
    public final void afterLoad(@NotNull final Runnable task) {
        this.afterLoad(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run after load.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void afterLoad(@NotNull final LifecyclePosition position,
                                @NotNull final Runnable task) {
        this.afterLoad.append(position, task);
    }

    /**
     * Reload the plugin.
     */
    public final void reload() {
        this.reload(true);
    }

    /**
     * Reload the plugin.
     *
     * @param cancelTasks If tasks should be cancelled.
     */
    public final void reload(final boolean cancelTasks) {
        this.getConfigHandler().updateConfigs();

        if (cancelTasks) {
            this.getScheduler().cancelAll();
        }

        this.handleLifecycle(this.onReload, this::handleReload);

        if (cancelTasks) {
            this.handleLifecycle(this.onCreateTasks, this::createTasks);
        }

        for (Extension extension : this.extensionLoader.getLoadedExtensions()) {
            extension.handleReload();
        }
    }

    /**
     * Add new task to run on reload.
     *
     * @param task The task.
     */
    public final void onReload(@NotNull final Runnable task) {
        this.onReload(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run on reload.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void onReload(@NotNull final LifecyclePosition position,
                               @NotNull final Runnable task) {
        this.onReload.append(position, task);
    }

    /**
     * Add new task to run on createTasks.
     *
     * @param task The task.
     */
    public final void onCreateTasks(@NotNull final Runnable task) {
        this.onCreateTasks(LifecyclePosition.END, task);
    }

    /**
     * Add new task to run on createTasks.
     *
     * @param position The position to run the task.
     * @param task     The task.
     */
    public final void onCreateTasks(@NotNull final LifecyclePosition position,
                                    @NotNull final Runnable task) {
        this.onCreateTasks.append(position, task);
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
     * Handle lifecycle.
     *
     * @param tasks   The tasks.
     * @param handler The handler.
     */
    private void handleLifecycle(@NotNull final ListMap<LifecyclePosition, Runnable> tasks,
                                 @NotNull final Runnable handler) {
        for (Runnable task : tasks.get(LifecyclePosition.START)) {
            try {
                task.run();
            } catch (final Exception e) {
                this.getLogger().log(Level.SEVERE, "Error while running lifecycle task!");
                this.getLogger().log(Level.SEVERE, "The plugin may not function properly");
                e.printStackTrace();
            }
        }

        try {
            handler.run();
        } catch (final Exception e) {
            this.getLogger().log(Level.SEVERE, "Error while running lifecycle task!");
            this.getLogger().log(Level.SEVERE, "The plugin may not function properly");
            e.printStackTrace();
        }

        for (Runnable task : tasks.get(LifecyclePosition.END)) {
            try {
                task.run();
            } catch (final Exception e) {
                this.getLogger().log(Level.SEVERE, "Error while running lifecycle task!");
                this.getLogger().log(Level.SEVERE, "The plugin may not function properly");
                e.printStackTrace();
            }
        }
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
     * The plugin-specific code to create tasks.
     * <p>
     * Override when needed.
     */
    protected void createTasks() {

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
     * Packet Listeners to be registered.
     *
     * @return A list of handle listeners.
     */
    protected List<PacketListener> loadPacketListeners() {
        return new ArrayList<>();
    }

    /**
     * All listeners to be registered.
     *
     * @return A list of all listeners.
     */
    protected List<Listener> loadListeners() {
        return new ArrayList<>();
    }

    /**
     * Useful for custom LangYml implementations.
     * <p>
     * Override if needed.
     *
     * @return lang.yml.
     */
    protected LangYml createLangYml() {
        try {
            return new LangYml(this);
        } catch (NullPointerException e) {
            this.getLogger().severe("Failed to load lang.yml!");
            this.getLogger().severe("For the developer of this plugin: make sure you have a lang.yml");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return null;
        }
    }

    /**
     * Useful for custom ConfigYml implementations.
     * <p>
     * Override if needed.
     *
     * @return config.yml.
     */
    protected ConfigYml createConfigYml() {
        try {
            return new ConfigYml(this);
        } catch (NullPointerException e) {
            this.getLogger().severe("Failed to load config.yml!");
            this.getLogger().severe("For the developer of this plugin: make sure you have a config.yml");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return null;
        }
    }

    /**
     * Create the display module for the plugin.
     *
     * @return The display module, or null.
     * @deprecated Use {@link #loadDisplayModules()} instead.
     */
    @Nullable
    @Deprecated(since = "6.72.0")
    protected DisplayModule createDisplayModule() {
        Preconditions.checkArgument(this.getDisplayModule() == null, "Display module exists!");

        return null;
    }

    /**
     * Load display modules.
     *
     * @return The display modules.
     */
    protected List<DisplayModule> loadDisplayModules() {
        return new ArrayList<>();
    }

    /**
     * Get the minimum version of eco to use the plugin.
     *
     * @return The version.
     * @deprecated Use {@link PluginProps#getEcoApiVersion()} instead, configure in eco.yml as eco-api-version.
     */
    @Deprecated(since = "6.77.0", forRemoval = true)
    public String getMinimumEcoVersion() {
        return this.getProps().getEcoApiVersion().toString();
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
        Preconditions.checkNotNull(proxyFactory, "Plugin does not support proxies!");

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
    @Nullable
    public static EcoPlugin getPlugin(@NotNull final String pluginName) {
        return Eco.get().getPluginByName(pluginName);
    }

    /**
     * Get all EcoPlugin names.
     *
     * @return The set of names.
     */
    @NotNull
    public static Set<String> getPluginNames() {
        return new HashSet<>(Eco.get().getLoadedPlugins());
    }

    /**
     * Get the plugin props. (eco.yml).
     *
     * @return The props.
     */
    @NotNull
    public PluginProps getProps() {
        return this.props;
    }

    /**
     * Get the polymart resource ID.
     *
     * @return The resource ID.
     */
    public int getResourceId() {
        return this.getProps().getResourceId();
    }

    /**
     * Get the bStats ID.
     *
     * @return The ID.
     */
    public int getBStatsId() {
        return this.getProps().getBStatsId();
    }

    /**
     * Get the proxy package.
     *
     * @return The package where proxies are contained.
     */
    public String getProxyPackage() {
        return this.getProps().getProxyPackage();
    }

    /**
     * Get the plugin color (uses legacy formatting).
     *
     * @return The color.
     */
    public String getColor() {
        return this.getProps().getColor();
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
    public @NotNull ConfigHandler getConfigHandler() {
        return this.configHandler;
    }

    /**
     * Get the plugin's display module.
     *
     * @return The display module.
     * @deprecated Use {@link #getDisplayModules()} instead.
     */
    @Nullable
    @Deprecated(since = "6.72.0", forRemoval = true)
    public DisplayModule getDisplayModule() {
        return this.displayModule;
    }

    /**
     * Get the plugin's display modules.
     *
     * @return The display modules.
     */
    public List<DisplayModule> getDisplayModules() {
        return ImmutableList.copyOf(this.displayModules);
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
        return this.getProps().isSupportingExtensions();
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

    /**
     * Get if all {@link com.willfp.eco.core.data.keys.PersistentDataKey}'s for this
     * plugin should be saved locally (via data.yml.) even if eco is using a database.
     *
     * @return If using local storage.
     */
    public boolean isUsingLocalStorage() {
        return this.configYml.isUsingLocalStorage();
    }

    @Override
    @NotNull
    public final String getID() {
        return Registry.tryFitPattern(this.getName());
    }

    @Override
    public @NotNull File getFile() {
        return super.getFile();
    }
}
