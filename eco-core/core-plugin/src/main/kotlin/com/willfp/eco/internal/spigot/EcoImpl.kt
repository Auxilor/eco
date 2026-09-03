package com.willfp.eco.internal.spigot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.FoliaSupport
import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.bstats.EcoMetricsChart
import com.willfp.eco.core.integrations.anticheat.AnticheatManager
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.customitems.CustomItemsManager
import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.hologram.EcoHologram
import com.willfp.eco.internal.spigot.hologram.HologramTracker
import com.willfp.eco.internal.spigot.proxies.HologramProxy
import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.PluginCommandBase
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.PlayerProfileResolver
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.datapack.DatapackContributor
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuType
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.gui.view.LocationViewBuilder
import com.willfp.eco.core.gui.view.MerchantViewBuilder
import com.willfp.eco.core.gui.view.ViewBuilder
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.packet.Packet
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.scheduling.Scheduler
import com.willfp.eco.core.version.Version
import com.willfp.eco.internal.EcoPropsParser
import com.willfp.eco.internal.command.EcoPluginCommand
import com.willfp.eco.internal.command.EcoSubcommand
import com.willfp.eco.internal.config.EcoConfigSection
import com.willfp.eco.internal.config.EcoLoadableConfig
import com.willfp.eco.internal.config.EcoUpdatableConfig
import com.willfp.eco.internal.config.handler.SimpleConfigHandler
import com.willfp.eco.internal.config.toMap
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue
import com.willfp.eco.internal.events.EcoEventManager
import com.willfp.eco.internal.extensions.EcoExtensionLoader
import com.willfp.eco.internal.factory.EcoMetadataValueFactory
import com.willfp.eco.internal.factory.EcoNamespacedKeyFactory
import com.willfp.eco.internal.factory.EcoRunnableFactory
import com.willfp.eco.internal.fast.SafeInternalNamespacedKeyFactory
import com.willfp.eco.internal.gui.MergedStateMenu
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.menu.renderedInventory
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder
import com.willfp.eco.internal.gui.view.EcoLocationViewBuilder
import com.willfp.eco.internal.gui.view.EcoMerchantViewBuilder
import com.willfp.eco.internal.gui.view.EcoViewBuilder
import com.willfp.eco.internal.integrations.PAPIExpansion
import com.willfp.eco.internal.logging.EcoLogger
import com.willfp.eco.internal.logging.NOOPLogger
import com.willfp.eco.internal.placeholder.PlaceholderParser
import com.willfp.eco.internal.proxy.EcoProxyFactory
import com.willfp.eco.internal.scheduling.EcoSchedulerBukkit
import com.willfp.eco.internal.scheduling.EcoSchedulerFolia
import com.willfp.eco.internal.spigot.data.DataYml
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.profiles.ProfileHandler
import com.willfp.eco.internal.spigot.integrations.bstats.MetricHandler
import com.willfp.eco.internal.spigot.math.ExpressionEvaluator
import com.willfp.eco.internal.spigot.math.api.EcoExpressionEnvironmentBuilder
import com.willfp.eco.internal.spigot.proxies.BukkitCommandsProxy
import com.willfp.eco.internal.spigot.proxies.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxies.DisplayNameProxy
import com.willfp.eco.internal.spigot.proxies.DummyEntityFactoryProxy
import com.willfp.eco.internal.spigot.proxies.EntityControllerFactoryProxy
import com.willfp.eco.internal.spigot.proxies.ExtendedPersistentDataContainerFactoryProxy
import com.willfp.eco.internal.spigot.proxies.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxies.MiniMessageTranslatorProxy
import com.willfp.eco.internal.spigot.proxies.PacketHandlerProxy
import com.willfp.eco.internal.spigot.proxies.PlayerHandlerProxy
import com.willfp.eco.internal.spigot.proxies.SNBTConverterProxy
import com.willfp.eco.internal.spigot.proxies.SkullProxy
import com.willfp.eco.internal.spigot.proxies.TPSProxy
import com.willfp.eco.internal.spigot.proxies.WaypointHandlerProxy
import java.net.URLClassLoader
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.view.MerchantView
import org.bukkit.inventory.view.builder.InventoryViewBuilder
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder
import org.bukkit.inventory.MenuType as BukkitMenuType
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer

private val loadedEcoPlugins = ConcurrentHashMap<String, EcoPlugin>()
private val DEFAULT_PROFILE_RESOLVER = PlayerProfileResolver { it.uniqueId }

@Suppress("UNUSED")
class EcoImpl : EcoSpigotPlugin(), Eco {
    override val dataYml = DataYml(this)

    override val profileHandler = ProfileHandler(this)

    val hologramTracker: HologramTracker by lazy { HologramTracker(this) }

    init {
        getProxy(CommonsInitializerProxy::class.java).init(this)
    }

    private val keyFactory = SafeInternalNamespacedKeyFactory()

    private val placeholderParser = PlaceholderParser(
        progressBarCharacter = this.configYml.getString("progress-bar.character").firstOrNull() ?: '|',
        progressBarBars = this.configYml.getInt("progress-bar.bars"),
        progressBarCompleteFormat = this.configYml.getString("progress-bar.complete-format"),
        progressBarInProgressFormat = this.configYml.getString("progress-bar.in-progress-format"),
        progressBarIncompleteFormat = this.configYml.getString("progress-bar.incomplete-format")
    )

    private val expressionEvaluator = ExpressionEvaluator(
        placeholderParser,
        this.configYml.getInt("math-cache-ttl").toLong()
    )

    /**
     * The return type must stay the [Scheduler] interface, and must stay explicit.
     *
     * [EcoSchedulerFolia] names Folia types, which are absent on Spigot. The JVM verifier
     * does not check assignability to an interface, so neither branch forces that class to
     * be resolved here; it is loaded only when its constructor actually runs, which happens
     * only on Folia. Narrowing this to a concrete type would make Spigot resolve it at
     * verification and every Spigot server would die at startup with NoClassDefFoundError.
     */
    override fun createScheduler(plugin: EcoPlugin): Scheduler =
        if (Prerequisite.HAS_FOLIA.isMet) {
            EcoSchedulerFolia(plugin)
        } else {
            EcoSchedulerBukkit(plugin)
        }

    override fun createEventManager(plugin: EcoPlugin) =
        EcoEventManager(plugin)

    override fun createNamespacedKeyFactory(plugin: EcoPlugin) =
        EcoNamespacedKeyFactory(plugin)

    override fun createMetadataValueFactory(plugin: EcoPlugin) =
        EcoMetadataValueFactory(plugin)

    override fun createRunnableFactory(plugin: EcoPlugin) =
        EcoRunnableFactory(plugin)

    override fun createExtensionLoader(plugin: EcoPlugin) =
        EcoExtensionLoader(plugin)

    override fun createConfigHandler(plugin: EcoPlugin) =
        SimpleConfigHandler()

    override fun createLogger(plugin: EcoPlugin) =
        EcoLogger(plugin)

    override fun getNOOPLogger() =
        NOOPLogger

    override fun createPAPIIntegration(plugin: EcoPlugin) {
        PAPIExpansion(plugin)
    }

    override fun getEcoPlugin(): EcoPlugin =
        this

    override fun createConfig(contents: String, type: ConfigType) =
        EcoConfigSection(type, type.toMap(contents))

    override fun createConfig(values: Map<String, Any>, type: ConfigType) =
        EcoConfigSection(type, values)

    override fun createLoadableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        type: ConfigType,
        requiresChangesToSave: Boolean
    ) = EcoLoadableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source,
        requiresChangesToSave
    )

    override fun createUpdatableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        type: ConfigType,
        requiresChangesToSave: Boolean,
        vararg updateBlacklist: String
    ) = EcoUpdatableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source,
        removeUnused,
        requiresChangesToSave,
        *updateBlacklist
    )

    override fun wrapConfigurationSection(bukkit: ConfigurationSection): Config {
        val config = createConfig(emptyMap(), ConfigType.YAML)
        for (key in bukkit.getKeys(true)) {
            config.set(key, bukkit.get(key))
        }

        return config
    }

    override fun createPluginCommand(
        parentDelegate: PluginCommandBase,
        plugin: EcoPlugin,
        name: String,
        permission: String,
        playersOnly: Boolean
    ) = EcoPluginCommand(
        parentDelegate,
        plugin,
        name,
        permission,
        playersOnly
    )

    override fun createSubcommand(
        parentDelegate: CommandBase,
        plugin: EcoPlugin,
        name: String,
        permission: String,
        playersOnly: Boolean
    ) = EcoSubcommand(
        parentDelegate,
        plugin,
        name,
        permission,
        playersOnly
    )

    override fun createDropQueue(player: Player) =
        if (this.configYml.getBool("use-fast-collated-drops"))
            EcoFastCollatedDropQueue(player) else EcoDropQueue(player)

    override fun getRegisteredPersistentDataKeys() =
        KeyRegistry.getRegisteredKeys()

    override fun registerPersistentKey(key: PersistentDataKey<*>) =
        KeyRegistry.registerKey(key)

    override fun createMenuBuilder(rows: Int, type: MenuType) =
        EcoMenuBuilder(rows, type.columns)

    override fun createSlotBuilder(provider: SlotProvider) =
        EcoSlotBuilder(provider)

    override fun blendMenuState(base: Menu, additional: Menu) =
        MergedStateMenu(base, additional)

    override fun <V : InventoryView> createViewBuilder(
        type: BukkitMenuType.Typed<V, out InventoryViewBuilder<V>>
    ): ViewBuilder<V> = EcoViewBuilder<V, InventoryViewBuilder<V>>(type.builder())

    override fun <V : InventoryView> createLocationViewBuilder(
        type: BukkitMenuType.Typed<V, LocationInventoryViewBuilder<V>>
    ): LocationViewBuilder<V> = EcoLocationViewBuilder(type.builder())

    override fun createMerchantViewBuilder(): MerchantViewBuilder<MerchantView> =
        EcoMerchantViewBuilder(BukkitMenuType.MERCHANT.builder())

    override fun clean(plugin: EcoPlugin) {
        // Prevent self-cleaning
        if (plugin == this) {
            return
        }

        if (plugin.proxyPackage.isNotEmpty()) {
            val factory = plugin.proxyFactory as EcoProxyFactory
            factory.clean()
        }

        loadedEcoPlugins.remove(plugin.name.lowercase())

        for (customItem in Items.getCustomItems()) {
            if (customItem.key.namespace.equals(plugin.name.lowercase(), ignoreCase = true)) {
                Items.removeCustomItem(customItem.key)
            }
        }

        for (customBlock in Blocks.getCustomBlocks()) {
            if (customBlock.key.namespace.equals(plugin.name.lowercase(), ignoreCase = true)) {
                Blocks.removeCustomBlock(customBlock.key)
            }
        }

        val classLoader = plugin::class.java.classLoader

        if (classLoader is URLClassLoader) {
            classLoader.close()
        }

        System.gc()
    }

    override fun createProxyFactory(plugin: EcoPlugin) =
        EcoProxyFactory(plugin)

    override fun addNewPlugin(plugin: EcoPlugin) {
        loadedEcoPlugins[plugin.name.lowercase()] = plugin
        loadedEcoPlugins[plugin.id] = plugin
    }

    override fun registerOutdatedPlugin(pluginName: String, requiredVersion: Version) =
        OutdatedPlugins.register(pluginName, requiredVersion)

    override fun getLoadedPlugins(): List<String> =
        loadedEcoPlugins.keys.toList()

    override fun getPluginByName(name: String): EcoPlugin? =
        loadedEcoPlugins[name.lowercase()]

    override fun createFastItemStack(itemStack: ItemStack) =
        getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)

    override fun registerBStats(plugin: EcoPlugin) =
        MetricHandler.createMetrics(plugin)

    override fun getAdventure() =
        bukkitAudiences

    override fun getServerProfile() =
        profileHandler.getServerProfile()

    override fun loadPlayerProfile(uuid: UUID) =
        profileHandler.getPlayerProfile(uuid)

    // Read from whichever thread touches player data, so publication has to be guaranteed.
    @Volatile
    private var playerProfileResolver = DEFAULT_PROFILE_RESOLVER

    override fun setPlayerProfileResolver(resolver: PlayerProfileResolver?) {
        playerProfileResolver = if (resolver == null) DEFAULT_PROFILE_RESOLVER else {
            // Wrapped rather than stored bare so every profile a player resolves to is recorded and
            // can be unloaded when they leave. Only wrapped when a resolver is actually set, so the
            // default path stays a plain UUID read.
            PlayerProfileResolver { player ->
                val profile = resolver.resolve(player)
                profileHandler.trackResolvedProfile(player.uniqueId, profile)
                profile
            }
        }
    }

    override fun getPlayerProfileResolver() = playerProfileResolver

    override fun createDummyEntity(location: Location): Entity {
        warnIfNotOwned(location, "Creating a dummy entity")

        return getProxy(DummyEntityFactoryProxy::class.java).createDummyEntity(location)
    }

    override fun createHologram(location: Location, options: HologramOptions): Hologram {
        warnIfNotOwned(location, "Creating a hologram")

        val handle = getProxy(HologramProxy::class.java).createHandle(location, options)
        return EcoHologram(handle, location, options, hologramTracker)
    }

    override fun handleEnable() {
        super.handleEnable()
        hologramTracker.start()
    }

    override fun handleDisable() {
        super.handleDisable()
        hologramTracker.shutdown()
    }

    override fun createNamespacedKey(namespace: String, key: String) =
        NamespacedKey(namespace, key)

    override fun getProps(existing: PluginProps?, plugin: Class<out EcoPlugin>) =
        existing ?: EcoPropsParser.parseForPlugin(plugin)

    override fun <T : Mob> createEntityController(mob: T): EntityController<T> {
        warnIfNotOwned(mob, "Creating an entity controller")

        return getProxy(EntityControllerFactoryProxy::class.java).createEntityController(mob)
    }

    override fun formatMiniMessage(message: String) =
        getProxy(MiniMessageTranslatorProxy::class.java).format(message)

    override fun adaptPdc(container: PersistentDataContainer) =
        getProxy(ExtendedPersistentDataContainerFactoryProxy::class.java).adapt(container)

    override fun newPdc() =
        getProxy(ExtendedPersistentDataContainerFactoryProxy::class.java).newPdc()

    override fun toSNBT(itemStack: ItemStack) =
        getProxy(SNBTConverterProxy::class.java).toSNBT(itemStack)

    override fun fromSNBT(snbt: String) =
        getProxy(SNBTConverterProxy::class.java).fromSNBT(snbt)

    override fun testableItemFromSNBT(snbt: String) =
        getProxy(SNBTConverterProxy::class.java).makeSNBTTestable(snbt)

    override fun getSkullTexture(meta: SkullMeta): String? =
        getProxy(SkullProxy::class.java).getSkullTexture(meta)

    override fun setSkullTexture(meta: SkullMeta, base64: String) =
        getProxy(SkullProxy::class.java).setSkullTexture(meta, base64)

    override fun getTPS() =
        getProxy(TPSProxy::class.java).getTPS()

    override fun isOwnedByCurrentRegion(location: Location): Boolean =
        if (Prerequisite.HAS_FOLIA.isMet) Bukkit.isOwnedByCurrentRegion(location) else true

    override fun isOwnedByCurrentRegion(entity: Entity): Boolean =
        if (Prerequisite.HAS_FOLIA.isMet) Bukkit.isOwnedByCurrentRegion(entity) else true

    /**
     * Warn once when a region-bound call is made from a thread that does not own the
     * region. Does not stop the call: Folia will refuse it and say so far more precisely
     * than this can, and off Folia there is nothing to warn about.
     */
    private fun warnIfNotOwned(location: Location, what: String) {
        if (isOwnedByCurrentRegion(location)) {
            return
        }

        FoliaSupport.isUnsupported("$what from outside its region")
    }

    /**
     * Warn once when a region-bound call is made from a thread that does not own the
     * region containing an entity. See [warnIfNotOwned] for the location overload.
     */
    private fun warnIfNotOwned(entity: Entity, what: String) {
        if (isOwnedByCurrentRegion(entity)) {
            return
        }

        FoliaSupport.isUnsupported("$what from outside its region")
    }

    /**
     * Warn once when a global-region-bound call is made from a thread that is not the
     * global region thread. Same idea as [warnIfNotOwned], for calls that belong on the
     * global region rather than on a location or entity's region. Does not stop the
     * call: see [warnIfNotOwned] for why.
     */
    private fun warnIfNotGlobalRegion(what: String) {
        if (!Prerequisite.HAS_FOLIA.isMet || Bukkit.isGlobalTickThread()) {
            return
        }

        FoliaSupport.isUnsupported("$what from outside the global region")
    }

    /**
     * Run on the region owning an entity, now if this thread already owns it.
     *
     * Off Folia the ownership check is always true, so this is a direct call.
     */
    private inline fun onEntity(entity: Entity, crossinline block: () -> Unit) {
        if (isOwnedByCurrentRegion(entity)) {
            block()
        } else {
            this.scheduler.on(entity).run { block() }
        }
    }

    /**
     * Run on the global region, now if this thread is already the global region thread.
     */
    private inline fun onGlobalRegion(crossinline block: () -> Unit) {
        if (!Prerequisite.HAS_FOLIA.isMet || Bukkit.isGlobalTickThread()) {
            block()
        } else {
            this.scheduler.global().run { block() }
        }
    }

    override fun evaluate(expression: String, context: PlaceholderContext) =
        expressionEvaluator.evaluate(expression, context)

    override fun createExpressionEnvironmentBuilder() =
        EcoExpressionEnvironmentBuilder()

    override fun getDatapackHandle(plugin: EcoPlugin) =
        datapackRegistry.handle(plugin)

    override fun registerDatapackContributor(plugin: EcoPlugin, contributor: DatapackContributor) =
        datapackRegistry.register(plugin, contributor)

    override fun isDatapackRestartPending() =
        datapackRegistry.restartPending

    override fun getOpenMenu(player: Player) =
        player.renderedInventory?.menu

    override fun addBukkitRecipeNoResend(recipe: Recipe) = onGlobalRegion {
        this.getProxy(CommonsInitializerProxy::class.java).addBukkitRecipeNoResend(recipe)
    }

    override fun reloadBukkitRecipes() = onGlobalRegion {
        this.getProxy(CommonsInitializerProxy::class.java).reloadBukkitRecipes()
    }

    // Not routed through onGlobalRegion: this returns a Boolean, and onGlobalRegion's
    // block is `() -> Unit`, so it cannot carry a result back from a scheduled task
    // without either blocking the caller or inventing a placeholder return value, neither
    // of which the task brief specifies. Runs inline, exactly as before, on both Paper
    // and Folia; warnIfNotGlobalRegion only warns (it never throws or reschedules) when
    // called off the global region on Folia. See task-19-report.md.
    override fun removeBukkitRecipeNoResend(key: NamespacedKey): Boolean {
        warnIfNotGlobalRegion("Removing a recipe without resending it")
        return this.getProxy(CommonsInitializerProxy::class.java).removeBukkitRecipeNoResend(key)
    }

    private var batchDepth = 0
    private var syncDuringBatch = false

    override fun syncCommands() {
        if (batchDepth > 0) {
            syncDuringBatch = true
            return
        }
        onGlobalRegion {
            this.getProxy(BukkitCommandsProxy::class.java).syncCommands()
        }
    }

    override fun beginCommandBatch() {
        batchDepth++
    }

    override fun endCommandBatch() {
        check(batchDepth > 0) { "endCommandBatch() called without matching beginCommandBatch()" }
        batchDepth--
        if (batchDepth == 0 && syncDuringBatch) {
            syncDuringBatch = false
            onGlobalRegion {
                this.getProxy(BukkitCommandsProxy::class.java).syncCommands()
            }
        }
    }

    override fun unregisterCommand(command: PluginCommandBase) = onGlobalRegion {
        this.getProxy(BukkitCommandsProxy::class.java).unregisterCommand(command)
    }

    override fun sendPacket(player: Player, packet: Packet) =
        this.getProxy(PacketHandlerProxy::class.java).sendPacket(player, packet)

    override fun showWaypoint(viewer: Player, id: UUID, location: Location, color: Int?) =
        this.getProxy(WaypointHandlerProxy::class.java).showWaypoint(viewer, id, location, color)

    override fun hideWaypoint(viewer: Player, id: UUID) =
        this.getProxy(WaypointHandlerProxy::class.java).hideWaypoint(viewer, id)

    override fun translatePlaceholders(text: String, context: PlaceholderContext) =
        placeholderParser.translatePlacholders(text, context)

    override fun getPlaceholderValue(plugin: EcoPlugin?, args: String, context: PlaceholderContext) =
        placeholderParser.getPlaceholderResult(plugin, args, context)

    override fun setClientsideDisplayName(entity: LivingEntity, player: Player, name: Component, visible: Boolean) =
        this.getProxy(DisplayNameProxy::class.java).setClientsideDisplayName(entity, player, name, visible)

    override fun giveExpAndApplyMending(player: Player, amount: Int, applyMending: Boolean) {
        onEntity(player) {
            getProxy(PlayerHandlerProxy::class.java).giveExpAndApplyMending(player, amount, applyMending)
        }
    }

    override fun getCustomCharts() = listOf(
        EcoMetricsChart.SimplePie("data_handler") { profileHandler.defaultHandler.id },
        EcoMetricsChart.SingleLine("loaded_eco_plugins") {
            loadedEcoPlugins.values.distinct().size
        },
        EcoMetricsChart.SingleLine("loaded_extensions") {
            loadedEcoPlugins.values.distinct()
                .sumOf { it.extensionLoader.getLoadedExtensions().size }
        },
        EcoMetricsChart.AdvancedPie("antigrief_integrations") {
            AntigriefManager.getRegisteredIntegrations()
                .associate { it.pluginName to 1 }
                .ifEmpty { null }
        },
        EcoMetricsChart.AdvancedPie("custom_item_integrations") {
            CustomItemsManager.getRegisteredIntegrations()
                .associate { it.pluginName to 1 }
                .ifEmpty { null }
        },
        EcoMetricsChart.AdvancedPie("anticheat_integrations") {
            AnticheatManager.getRegisteredIntegrations()
                .associate { it.pluginName to 1 }
                .ifEmpty { null }
        }
    )
}
