package com.willfp.eco.internal.spigot

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Handler
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.data.ExtendedPersistentDataContainer
import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration
import com.willfp.eco.core.items.SNBTHandler
import com.willfp.eco.internal.EcoCleaner
import com.willfp.eco.internal.EcoPropsParser
import com.willfp.eco.internal.Plugins
import com.willfp.eco.internal.config.EcoConfigFactory
import com.willfp.eco.internal.config.EcoConfigHandler
import com.willfp.eco.internal.drops.EcoDropQueueFactory
import com.willfp.eco.internal.events.EcoEventManager
import com.willfp.eco.internal.extensions.EcoExtensionLoader
import com.willfp.eco.internal.factory.EcoMetadataValueFactory
import com.willfp.eco.internal.factory.EcoNamespacedKeyFactory
import com.willfp.eco.internal.factory.EcoRunnableFactory
import com.willfp.eco.internal.fast.FastInternalNamespacedKeyFactory
import com.willfp.eco.internal.fast.InternalNamespacedKeyFactory
import com.willfp.eco.internal.fast.SafeInternalNamespacedKeyFactory
import com.willfp.eco.internal.gui.EcoGUIFactory
import com.willfp.eco.internal.integrations.PlaceholderIntegrationPAPI
import com.willfp.eco.internal.logging.EcoLogger
import com.willfp.eco.internal.proxy.EcoProxyFactory
import com.willfp.eco.internal.scheduling.EcoScheduler
import com.willfp.eco.internal.spigot.data.DataYml
import com.willfp.eco.internal.spigot.data.EcoKeyRegistry
import com.willfp.eco.internal.spigot.data.EcoProfileHandler
import com.willfp.eco.internal.spigot.data.storage.HandlerType
import com.willfp.eco.internal.spigot.integrations.bstats.MetricHandler
import com.willfp.eco.internal.spigot.items.EcoSNBTHandler
import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.DummyEntityFactoryProxy
import com.willfp.eco.internal.spigot.proxy.EntityControllerFactoryProxy
import com.willfp.eco.internal.spigot.proxy.ExtendedPersistentDataContainerFactoryProxy
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.MiniMessageTranslatorProxy
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import java.util.logging.Logger

@Suppress("UNUSED")
class EcoHandler : EcoSpigotPlugin(), Handler {
    init {
        getProxy(CommonsInitializerProxy::class.java).init()
    }

    override val dataYml = DataYml(this)

    private val cleaner = EcoCleaner()

    private var adventure: BukkitAudiences? = null

    private val keyRegistry = EcoKeyRegistry()

    private val playerProfileHandler = EcoProfileHandler(
        HandlerType.valueOf(this.configYml.getString("data-handler").uppercase()),
        this
    )

    private val snbtHandler = EcoSNBTHandler(this)

    @Suppress("RedundantNullableReturnType")
    private val keyFactory: InternalNamespacedKeyFactory? =
        if (this.configYml.getBool("use-safer-namespacedkey-creation"))
            SafeInternalNamespacedKeyFactory() else FastInternalNamespacedKeyFactory()

    override fun createScheduler(plugin: EcoPlugin): EcoScheduler =
        EcoScheduler(plugin)

    override fun createEventManager(plugin: EcoPlugin): EcoEventManager =
        EcoEventManager(plugin)

    override fun createNamespacedKeyFactory(plugin: EcoPlugin): EcoNamespacedKeyFactory =
        EcoNamespacedKeyFactory(plugin)

    override fun createMetadataValueFactory(plugin: EcoPlugin): EcoMetadataValueFactory =
        EcoMetadataValueFactory(plugin)

    override fun createRunnableFactory(plugin: EcoPlugin): EcoRunnableFactory =
        EcoRunnableFactory(plugin)

    override fun createExtensionLoader(plugin: EcoPlugin): EcoExtensionLoader =
        EcoExtensionLoader(plugin)

    override fun createConfigHandler(plugin: EcoPlugin): EcoConfigHandler =
        EcoConfigHandler(plugin)

    override fun createLogger(plugin: EcoPlugin): Logger =
        EcoLogger(plugin)

    override fun createPAPIIntegration(plugin: EcoPlugin): PlaceholderIntegration =
        PlaceholderIntegrationPAPI(plugin)

    override fun getEcoPlugin(): EcoPlugin =
        this

    override fun getConfigFactory(): EcoConfigFactory =
        EcoConfigFactory

    override fun getDropQueueFactory(): EcoDropQueueFactory =
        EcoDropQueueFactory

    override fun getGUIFactory(): EcoGUIFactory =
        EcoGUIFactory

    override fun getCleaner(): EcoCleaner =
        cleaner

    override fun createProxyFactory(plugin: EcoPlugin): EcoProxyFactory =
        EcoProxyFactory(plugin)

    override fun addNewPlugin(plugin: EcoPlugin) {
        Plugins.LOADED_ECO_PLUGINS[plugin.name.lowercase()] = plugin
    }

    override fun getLoadedPlugins(): List<String> =
        Plugins.LOADED_ECO_PLUGINS.keys.toMutableList()

    override fun getPluginByName(name: String): EcoPlugin? =
        Plugins.LOADED_ECO_PLUGINS[name.lowercase()]

    override fun createFastItemStack(itemStack: ItemStack): FastItemStack =
        getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)

    override fun registerBStats(plugin: EcoPlugin) =
        MetricHandler.createMetrics(plugin)

    override fun getAdventure(): BukkitAudiences? =
        adventure

    override fun getKeyRegistry(): EcoKeyRegistry =
        keyRegistry

    override fun getProfileHandler(): EcoProfileHandler =
        playerProfileHandler

    fun setAdventure(adventure: BukkitAudiences) {
        this.adventure = adventure
    }

    override fun createDummyEntity(location: Location): Entity =
        getProxy(DummyEntityFactoryProxy::class.java).createDummyEntity(location)

    @Suppress("DEPRECATION")
    override fun createNamespacedKey(namespace: String, key: String): NamespacedKey =
        keyFactory?.create(namespace, key) ?: NamespacedKey(namespace, key)

    override fun getProps(existing: PluginProps?, plugin: Class<out EcoPlugin>): PluginProps =
        existing ?: EcoPropsParser.parseForPlugin(plugin)

    override fun <T : Mob> createEntityController(mob: T): EntityController<T> =
        getProxy(EntityControllerFactoryProxy::class.java).createEntityController(mob)

    override fun formatMiniMessage(message: String): String =
        getProxy(MiniMessageTranslatorProxy::class.java).format(message)

    override fun adaptPdc(container: PersistentDataContainer): ExtendedPersistentDataContainer =
        getProxy(ExtendedPersistentDataContainerFactoryProxy::class.java).adapt(container)

    override fun newPdc(): PersistentDataContainer =
        getProxy(ExtendedPersistentDataContainerFactoryProxy::class.java).newPdc()

    override fun getSNBTHandler(): SNBTHandler =
        snbtHandler
}
