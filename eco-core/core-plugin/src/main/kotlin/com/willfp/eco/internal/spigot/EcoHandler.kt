package com.willfp.eco.internal.spigot

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Handler
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.entities.ai.EntityController
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration
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
import com.willfp.eco.internal.spigot.integrations.bstats.MetricHandler
import com.willfp.eco.internal.spigot.proxy.CommonsInitializerProxy
import com.willfp.eco.internal.spigot.proxy.DummyEntityFactoryProxy
import com.willfp.eco.internal.spigot.proxy.EntityControllerFactoryProxy
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.MiniMessageTranslatorProxy
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.inventory.ItemStack
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
    private val playerProfileHandler = EcoProfileHandler(this.configYml.getBool("mysql.enabled"), this)

    @Suppress("RedundantNullableReturnType")
    private val keyFactory: InternalNamespacedKeyFactory? =
        if (this.configYml.getBool("use-safer-namespacedkey-creation"))
            SafeInternalNamespacedKeyFactory() else FastInternalNamespacedKeyFactory()

    override fun createScheduler(plugin: EcoPlugin): EcoScheduler = EcoScheduler(plugin)

    override fun createEventManager(plugin: EcoPlugin) = EcoEventManager(plugin)

    override fun createNamespacedKeyFactory(plugin: EcoPlugin): EcoNamespacedKeyFactory {
        return EcoNamespacedKeyFactory(plugin)
    }

    override fun createMetadataValueFactory(plugin: EcoPlugin): EcoMetadataValueFactory {
        return EcoMetadataValueFactory(plugin)
    }

    override fun createRunnableFactory(plugin: EcoPlugin): EcoRunnableFactory {
        return EcoRunnableFactory(plugin)
    }

    override fun createExtensionLoader(plugin: EcoPlugin): EcoExtensionLoader {
        return EcoExtensionLoader(plugin)
    }

    override fun createConfigHandler(plugin: EcoPlugin): EcoConfigHandler {
        return EcoConfigHandler(plugin)
    }

    override fun createLogger(plugin: EcoPlugin): Logger {
        return EcoLogger(plugin)
    }

    override fun createPAPIIntegration(plugin: EcoPlugin): PlaceholderIntegration {
        return PlaceholderIntegrationPAPI(plugin)
    }

    override fun getEcoPlugin(): EcoPlugin {
        return this
    }

    override fun getConfigFactory(): EcoConfigFactory {
        return EcoConfigFactory
    }

    override fun getDropQueueFactory(): EcoDropQueueFactory {
        return EcoDropQueueFactory()
    }

    override fun getGUIFactory(): EcoGUIFactory {
        return EcoGUIFactory()
    }

    override fun getCleaner(): EcoCleaner {
        return cleaner
    }

    override fun createProxyFactory(plugin: EcoPlugin): EcoProxyFactory {
        return EcoProxyFactory(plugin)
    }

    override fun addNewPlugin(plugin: EcoPlugin) {
        Plugins.LOADED_ECO_PLUGINS[plugin.name.lowercase()] = plugin
    }

    override fun getLoadedPlugins(): List<String> {
        return Plugins.LOADED_ECO_PLUGINS.keys.toMutableList()
    }

    override fun getPluginByName(name: String): EcoPlugin? {
        return Plugins.LOADED_ECO_PLUGINS[name.lowercase()]
    }

    override fun createFastItemStack(itemStack: ItemStack): FastItemStack {
        return getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)
    }

    override fun registerBStats(plugin: EcoPlugin) {
        MetricHandler.createMetrics(plugin)
    }

    override fun getAdventure(): BukkitAudiences? {
        return adventure
    }

    override fun getKeyRegistry(): EcoKeyRegistry {
        return keyRegistry
    }

    override fun getProfileHandler(): EcoProfileHandler {
        return playerProfileHandler
    }

    fun setAdventure(adventure: BukkitAudiences) {
        this.adventure = adventure
    }

    override fun createDummyEntity(location: Location): Entity {
        return getProxy(DummyEntityFactoryProxy::class.java).createDummyEntity(location)
    }

    override fun createNamespacedKey(namespace: String, key: String): NamespacedKey {
        @Suppress("DEPRECATION")
        return keyFactory?.create(namespace, key) ?: NamespacedKey(namespace, key)
    }

    override fun getProps(existing: PluginProps?, plugin: Class<out EcoPlugin>): PluginProps {
        return existing ?: EcoPropsParser.parseForPlugin(plugin)
    }

    override fun <T : Mob> createEntityController(mob: T): EntityController<T> {
        return getProxy(EntityControllerFactoryProxy::class.java).createEntityController(mob)
    }

    override fun formatMiniMessage(message: String): String {
        return getProxy(MiniMessageTranslatorProxy::class.java).format(message)
    }
}
