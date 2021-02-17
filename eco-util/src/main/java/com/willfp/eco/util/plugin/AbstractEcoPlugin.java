package com.willfp.eco.util.plugin;

import com.willfp.eco.internal.arrows.ArrowDataListener;
import com.willfp.eco.internal.bukkit.events.EcoEventManager;
import com.willfp.eco.internal.bukkit.logging.EcoLogger;
import com.willfp.eco.internal.bukkit.scheduling.EcoScheduler;
import com.willfp.eco.internal.extensions.EcoExtensionLoader;
import com.willfp.eco.util.bukkit.events.EventManager;
import com.willfp.eco.util.bukkit.keys.NamespacedKeyFactory;
import com.willfp.eco.util.bukkit.logging.Logger;
import com.willfp.eco.util.bukkit.meta.MetadataValueFactory;
import com.willfp.eco.util.bukkit.scheduling.RunnableFactory;
import com.willfp.eco.util.bukkit.scheduling.Scheduler;
import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.config.configs.Config;
import com.willfp.eco.util.config.configs.Lang;
import com.willfp.eco.util.config.updating.ConfigHandler;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.display.DisplayModule;
import com.willfp.eco.util.extensions.loader.ExtensionLoader;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.util.integrations.placeholder.plugins.PlaceholderIntegrationPAPI;
import com.willfp.eco.util.optional.Prerequisite;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.eco.util.updater.UpdateChecker;
import lombok.Getter;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
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
import java.util.stream.Collectors;

public abstract class AbstractEcoPlugin extends JavaPlugin {
    /**
     * Loaded eco plugins.
     */
    public static final List<String> LOADED_ECO_PLUGINS = new ArrayList<>();

    /**
     * The name of the plugin.
     */
    @Getter
    private final String pluginName;

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
     * Set of external plugin integrations.
     */
    private final List<IntegrationLoader> integrationLoaders = new ArrayList<>();

    /**
     * Set of classes to be processed on config update.
     */
    private final List<Class<?>> updatableClasses = new ArrayList<>();

    /**
     * The internal plugin logger.
     */
    @Getter
    private final Logger log;

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
    private final Config configYml;

    /**
     * Lang.yml.
     */
    @Getter
    private final Lang langYml;

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
     * The internal factory to produce {@link com.willfp.eco.util.bukkit.scheduling.EcoBukkitRunnable}s.
     */
    @Getter
    private final RunnableFactory runnableFactory;

    /**
     * The loader for all plugin extensions.
     *
     * @see com.willfp.eco.util.extensions.Extension
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
    @Nullable
    private final DisplayModule displayModule;

    /**
     * If the server is running an outdated version of the plugin.
     */
    @Getter
    private boolean outdated = false;

    /**
     * Create a new plugin.
     *
     * @param pluginName   The name of the plugin.
     * @param resourceId   The spigot resource ID for the plugin.
     * @param bStatsId     The bStats resource ID for the plugin.
     * @param proxyPackage The package where proxy implementations are stored.
     * @param color        The color of the plugin (used in messages, such as &a, &b)
     */
    protected AbstractEcoPlugin(@NotNull final String pluginName,
                                final int resourceId,
                                final int bStatsId,
                                @NotNull final String proxyPackage,
                                @NotNull final String color) {
        this.pluginName = pluginName;
        this.resourceId = resourceId;
        this.bStatsId = bStatsId;
        this.proxyPackage = proxyPackage;
        this.color = color;
        this.displayModule = createDisplayModule();

        this.log = new EcoLogger(this);
        this.scheduler = new EcoScheduler(this);
        this.eventManager = new EcoEventManager(this);
        this.namespacedKeyFactory = new NamespacedKeyFactory(this);
        this.metadataValueFactory = new MetadataValueFactory(this);
        this.runnableFactory = new RunnableFactory(this);
        this.extensionLoader = new EcoExtensionLoader(this);
        this.configHandler = new ConfigHandler(this);

        this.langYml = new Lang(this);
        this.configYml = new Config(this);

        LOADED_ECO_PLUGINS.add(this.getName().toLowerCase());
    }

    /**
     * Default code to be executed on plugin enable.
     */
    @Override
    public final void onEnable() {
        super.onLoad();

        this.getLog().info("");
        this.getLog().info("Loading " + this.color + this.pluginName);

        this.getEventManager().registerListener(new ArrowDataListener(this));

        new UpdateChecker(this).getVersion(version -> {
            DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(this.getDescription().getVersion());
            DefaultArtifactVersion mostRecentVersion = new DefaultArtifactVersion(version);
            if (!(currentVersion.compareTo(mostRecentVersion) > 0 || currentVersion.equals(mostRecentVersion))) {
                this.outdated = true;
                this.getScheduler().runTimer(() -> {
                    this.getLog().info("&c " + this.pluginName + " is out of date! (Version " + this.getDescription().getVersion() + ")");
                    this.getLog().info("&cThe newest version is &f" + version);
                    this.getLog().info("&cDownload the new version!");
                }, 0, 864000);
            }
        });

        new Metrics(this, this.bStatsId);

        Set<String> enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.toSet());

        if (enabledPlugins.contains("PlaceholderAPI")) {
            this.loadedIntegrations.add("PlaceholderAPI");
            PlaceholderManager.addIntegration(new PlaceholderIntegrationPAPI(this));
        }

        this.getIntegrationLoaders().forEach((integrationLoader -> {
            if (enabledPlugins.contains(integrationLoader.getPluginName())) {
                this.loadedIntegrations.add(integrationLoader.getPluginName());
                integrationLoader.load();
            }
        }));

        this.getLog().info("Loaded integrations: " + String.join(", ", this.getLoadedIntegrations()));

        Prerequisite.update();

        this.getPacketAdapters().forEach(abstractPacketAdapter -> {
            if (!abstractPacketAdapter.isPostLoad()) {
                abstractPacketAdapter.register();
            }
        });

        updatableClasses.addAll(this.getUpdatableClasses());

        this.getListeners().forEach(listener -> this.getEventManager().registerListener(listener));

        this.getCommands().forEach(AbstractCommand::register);

        this.getScheduler().runLater(this::afterLoad, 1);

        if (this.getDisplayModule() != null) {
            Display.registerDisplayModule(this.getDisplayModule());
        }

        this.updatableClasses.forEach(clazz -> this.getConfigHandler().registerUpdatableClass(clazz));

        this.enable();

        this.getLog().info("");
    }

    /**
     * Default code to be executed on plugin disable.
     */
    @Override
    public final void onDisable() {
        super.onDisable();

        this.getEventManager().unregisterAllListeners();
        this.getScheduler().cancelAll();

        this.disable();
    }

    /**
     * Default code to be executed on plugin load.
     */
    @Override
    public final void onLoad() {
        super.onLoad();

        this.load();
    }

    /**
     * Default code to be executed after the server is up.
     */
    public final void afterLoad() {
        this.getPacketAdapters().forEach(abstractPacketAdapter -> {
            if (abstractPacketAdapter.isPostLoad()) {
                abstractPacketAdapter.register();
            }
        });

        if (!Prerequisite.HAS_PAPER.isMet()) {
            this.getLog().error("");
            this.getLog().error("----------------------------");
            this.getLog().error("");
            this.getLog().error("You don't seem to be running paper!");
            this.getLog().error("Paper is strongly recommended for all servers,");
            this.getLog().error("and some things may not function properly without it");
            this.getLog().error("Download Paper from &fhttps://papermc.io");
            this.getLog().error("");
            this.getLog().error("----------------------------");
            this.getLog().error("");
        }

        this.postLoad();

        this.reload();

        this.getLog().info("Loaded " + this.color + this.pluginName);
    }

    /**
     * Default code to be executed on plugin reload.
     */
    public final void reload() {
        this.getConfigYml().update();
        this.getLangYml().update();

        this.getConfigHandler().callUpdate();
        this.getConfigHandler().callUpdate(); // Call twice to fix issues
        this.getScheduler().cancelAll();

        this.onReload();
    }

    /**
     * The plugin-specific code to be executed on enable.
     */
    public abstract void enable();

    /**
     * The plugin-specific code to be executed on disable.
     */
    public abstract void disable();

    /**
     * The plugin-specific code to be executed on load.
     */
    public abstract void load();

    /**
     * The plugin-specific code to be executed on reload.
     */
    public abstract void onReload();

    /**
     * The plugin-specific code to be executed after the server is up.
     */
    public abstract void postLoad();

    /**
     * The plugin-specific integrations to be tested and loaded.
     *
     * @return A list of integrations.
     */
    public abstract List<IntegrationLoader> getIntegrationLoaders();

    /**
     * The command to be registered.
     *
     * @return A list of commands.
     */
    public abstract List<AbstractCommand> getCommands();

    /**
     * ProtocolLib packet adapters to be registered.
     * <p>
     * If the plugin does not require ProtocolLib this can be left empty.
     *
     * @return A list of packet adapters.
     */
    public abstract List<AbstractPacketAdapter> getPacketAdapters();

    /**
     * All listeners to be registered.
     *
     * @return A list of all listeners.
     */
    public abstract List<Listener> getListeners();

    /**
     * All updatable classes.
     *
     * @return A list of all updatable classes.
     */
    public abstract List<Class<?>> getUpdatableClasses();

    /**
     * Create the display module for the plugin.
     *
     * @return The display module, or null.
     */
    @Nullable
    protected abstract DisplayModule createDisplayModule();
}
