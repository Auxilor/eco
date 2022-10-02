package com.willfp.eco.internal.spigot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuType
import com.willfp.eco.core.gui.slot.functional.SlotProvider
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.placeholder.AdditionalPlayer
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.internal.EcoPropsParser
import com.willfp.eco.internal.config.EcoConfigHandler
import com.willfp.eco.internal.config.EcoConfigSection
import com.willfp.eco.internal.config.EcoLoadableConfig
import com.willfp.eco.internal.config.EcoUpdatableConfig
import com.willfp.eco.internal.config.toMap
import com.willfp.eco.internal.drops.EcoDropQueue
import com.willfp.eco.internal.drops.EcoFastCollatedDropQueue
import com.willfp.eco.internal.events.EcoEventManager
import com.willfp.eco.internal.extensions.EcoExtensionLoader
import com.willfp.eco.internal.factory.EcoMetadataValueFactory
import com.willfp.eco.internal.factory.EcoNamespacedKeyFactory
import com.willfp.eco.internal.factory.EcoRunnableFactory
import com.willfp.eco.internal.fast.FastInternalNamespacedKeyFactory
import com.willfp.eco.internal.fast.InternalNamespacedKeyFactory
import com.willfp.eco.internal.fast.SafeInternalNamespacedKeyFactory
import com.willfp.eco.internal.gui.MergedStateMenu
import com.willfp.eco.internal.gui.menu.EcoMenuBuilder
import com.willfp.eco.internal.gui.menu.renderedInventory
import com.willfp.eco.internal.gui.slot.EcoSlotBuilder
import com.willfp.eco.internal.integrations.PlaceholderIntegrationPAPI
import com.willfp.eco.internal.logging.EcoLogger
import com.willfp.eco.internal.proxy.EcoProxyFactory
import com.willfp.eco.internal.scheduling.EcoScheduler
import com.willfp.eco.internal.spigot.data.DataYml
import com.willfp.eco.internal.spigot.data.EcoProfileHandler
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.storage.HandlerType
import com.willfp.eco.internal.spigot.integrations.bstats.MetricHandler
import com.willfp.eco.internal.spigot.math.evaluateExpression
import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.DummyEntityFactoryProxy
import com.willfp.eco.internal.spigot.proxy.EntityControllerFactoryProxy
import com.willfp.eco.internal.spigot.proxy.ExtendedPersistentDataContainerFactoryProxy
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.MiniMessageTranslatorProxy
import com.willfp.eco.internal.spigot.proxy.SNBTConverterProxy
import com.willfp.eco.internal.spigot.proxy.SkullProxy
import com.willfp.eco.internal.spigot.proxy.TPSProxy
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataContainer
import java.net.URLClassLoader
import java.util.UUID

private val loadedEcoPlugins = mutableMapOf<String, EcoPlugin>()

@Suppress("UNUSED")
class EcoImpl : EcoSpigotPlugin(), Eco {
    override val dataYml = DataYml(this)

    override val profileHandler = EcoProfileHandler(
        HandlerType.valueOf(this.configYml.getString("data-handler").uppercase()),
        this
    )

    init {
        getProxy(CommonsInitializerProxy::class.java).init()
    }

    private var adventure: BukkitAudiences? = if (!Prerequisite.HAS_PAPER.isMet) {
        BukkitAudiences.create(this)
    } else null

    @Suppress("RedundantNullableReturnType")
    private val keyFactory: InternalNamespacedKeyFactory? =
        if (this.configYml.getBool("use-safer-namespacedkey-creation"))
            SafeInternalNamespacedKeyFactory() else FastInternalNamespacedKeyFactory()

    override fun createScheduler(plugin: EcoPlugin) =
        EcoScheduler(plugin)

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
        EcoConfigHandler(plugin)

    override fun createLogger(plugin: EcoPlugin) =
        EcoLogger(plugin)

    override fun createPAPIIntegration(plugin: EcoPlugin) =
        PlaceholderIntegrationPAPI(plugin)

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

    override fun createDropQueue(player: Player) = if (this.configYml.getBool("use-fast-collated-drops"))
        EcoFastCollatedDropQueue(player) else EcoDropQueue(player)

    override fun getPersistentDataKeyFrom(namespacedKey: NamespacedKey) =
        KeyRegistry.getKeyFrom(namespacedKey)

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

    override fun clean(plugin: EcoPlugin) {
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
    }

    override fun getLoadedPlugins(): List<String> =
        loadedEcoPlugins.keys.toList()

    override fun getPluginByName(name: String): EcoPlugin? =
        loadedEcoPlugins[name.lowercase()]

    override fun createFastItemStack(itemStack: ItemStack) =
        getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)

    override fun registerBStats(plugin: EcoPlugin) =
        MetricHandler.createMetrics(plugin)

    override fun getAdventure() =
        adventure

    override fun getServerProfile() =
        profileHandler.loadServerProfile()

    override fun loadPlayerProfile(uuid: UUID) =
        profileHandler.load(uuid)

    override fun saveAllProfiles() =
        profileHandler.save()

    override fun savePersistentDataKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) =
        profileHandler.saveKeysFor(uuid, keys)

    override fun unloadPlayerProfile(uuid: UUID) =
        profileHandler.unloadPlayer(uuid)

    override fun createDummyEntity(location: Location): Entity =
        getProxy(DummyEntityFactoryProxy::class.java).createDummyEntity(location)

    @Suppress("DEPRECATION")
    override fun createNamespacedKey(namespace: String, key: String) =
        keyFactory?.create(namespace, key) ?: NamespacedKey(namespace, key)

    override fun getProps(existing: PluginProps?, plugin: Class<out EcoPlugin>) =
        existing ?: EcoPropsParser.parseForPlugin(plugin)

    override fun <T : Mob> createEntityController(mob: T) =
        getProxy(EntityControllerFactoryProxy::class.java).createEntityController(mob)

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

    override fun evaluate(
        expression: String,
        player: Player?,
        injectable: PlaceholderInjectable,
        additionalPlayers: MutableCollection<AdditionalPlayer>
    ) = evaluateExpression(expression, player, injectable, additionalPlayers)

    override fun getOpenMenu(player: Player) =
        player.renderedInventory?.menu
}
