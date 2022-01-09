package com.willfp.eco.internal.spigot

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Handler
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration
import com.willfp.eco.internal.EcoCleaner
import com.willfp.eco.internal.Plugins
import com.willfp.eco.internal.config.EcoConfigFactory
import com.willfp.eco.internal.config.updating.EcoConfigHandler
import com.willfp.eco.internal.drops.EcoDropQueueFactory
import com.willfp.eco.internal.events.EcoEventManager
import com.willfp.eco.internal.extensions.EcoExtensionLoader
import com.willfp.eco.internal.factory.EcoMetadataValueFactory
import com.willfp.eco.internal.factory.EcoNamespacedKeyFactory
import com.willfp.eco.internal.factory.EcoRunnableFactory
import com.willfp.eco.internal.gui.EcoGUIFactory
import com.willfp.eco.internal.integrations.PlaceholderIntegrationPAPI
import com.willfp.eco.internal.logging.EcoLogger
import com.willfp.eco.internal.proxy.EcoProxyFactory
import com.willfp.eco.internal.requirement.EcoRequirementFactory
import com.willfp.eco.internal.scheduling.EcoScheduler
import com.willfp.eco.internal.spigot.data.EcoKeyRegistry
import com.willfp.eco.internal.spigot.data.EcoPlayerProfileHandler
import com.willfp.eco.internal.spigot.data.storage.MySQLDataHandler
import com.willfp.eco.internal.spigot.data.storage.YamlDataHandler
import com.willfp.eco.internal.spigot.integrations.bstats.MetricHandler
import com.willfp.eco.internal.spigot.proxy.DummyEntityProxy
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import java.util.logging.Logger

@Suppress("UNUSED")
class EcoHandler : EcoSpigotPlugin(), Handler {
    private val cleaner = EcoCleaner()
    private val requirementFactory = EcoRequirementFactory()
    private var adventure: BukkitAudiences? = null
    private val keyRegistry = EcoKeyRegistry(this)
    private val playerProfileHandler = EcoPlayerProfileHandler(
        if (this.configYml.getBool("mysql.enabled"))
            MySQLDataHandler(this) else YamlDataHandler(this)
    )

    override fun createScheduler(plugin: EcoPlugin): EcoScheduler {
        return EcoScheduler(plugin)
    }

    override fun createEventManager(plugin: EcoPlugin): EcoEventManager {
        return EcoEventManager(plugin)
    }

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
        return EcoConfigFactory()
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

    override fun getRequirementFactory(): EcoRequirementFactory {
        return requirementFactory
    }

    override fun getAdventure(): BukkitAudiences? {
        return adventure
    }

    override fun getKeyRegistry(): EcoKeyRegistry {
        return keyRegistry
    }

    override fun getPlayerProfileHandler(): EcoPlayerProfileHandler {
        return playerProfileHandler
    }

    fun setAdventure(adventure: BukkitAudiences) {
        this.adventure = adventure
    }

    override fun createDummyEntity(location: Location): Entity {
        return getProxy(DummyEntityProxy::class.java).createDummyEntity(location)
    }
}