package com.willfp.eco.spigot

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Handler
import com.willfp.eco.core.config.updating.ConfigHandler
import com.willfp.eco.core.config.wrapper.ConfigFactory
import com.willfp.eco.core.drops.DropQueueFactory
import com.willfp.eco.core.events.EventManager
import com.willfp.eco.core.extensions.ExtensionLoader
import com.willfp.eco.core.factory.MetadataValueFactory
import com.willfp.eco.core.factory.NamespacedKeyFactory
import com.willfp.eco.core.factory.RunnableFactory
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.gui.GUIFactory
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration
import com.willfp.eco.core.proxy.Cleaner
import com.willfp.eco.core.proxy.ProxyFactory
import com.willfp.eco.core.scheduling.Scheduler
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
import com.willfp.eco.internal.scheduling.EcoScheduler
import com.willfp.eco.proxy.FastItemStackFactoryProxy
import org.bstats.bukkit.Metrics
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.logging.Logger

@Suppress("UNUSED")
class EcoHandler : EcoSpigotPlugin(), Handler {
    private val cleaner = EcoCleaner()

    override fun createScheduler(plugin: EcoPlugin): Scheduler {
        return EcoScheduler(plugin)
    }

    override fun createEventManager(plugin: EcoPlugin): EventManager {
        return EcoEventManager(plugin)
    }

    override fun createNamespacedKeyFactory(plugin: EcoPlugin): NamespacedKeyFactory {
        return EcoNamespacedKeyFactory(plugin)
    }

    override fun createMetadataValueFactory(plugin: EcoPlugin): MetadataValueFactory {
        return EcoMetadataValueFactory(plugin)
    }

    override fun createRunnableFactory(plugin: EcoPlugin): RunnableFactory {
        return EcoRunnableFactory(plugin)
    }

    override fun createExtensionLoader(plugin: EcoPlugin): ExtensionLoader {
        return EcoExtensionLoader(plugin)
    }

    override fun createConfigHandler(plugin: EcoPlugin): ConfigHandler {
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

    override fun getConfigFactory(): ConfigFactory {
        return EcoConfigFactory()
    }

    override fun getDropQueueFactory(): DropQueueFactory {
        return EcoDropQueueFactory()
    }

    override fun getGUIFactory(): GUIFactory {
        return EcoGUIFactory()
    }

    override fun getCleaner(): Cleaner {
        return cleaner
    }

    override fun createProxyFactory(plugin: EcoPlugin): ProxyFactory {
        return EcoProxyFactory(plugin)
    }

    override fun addNewPlugin(plugin: EcoPlugin) {
        Plugins.LOADED_ECO_PLUGINS[plugin.name.lowercase()] = plugin
    }

    override fun getLoadedPlugins(): List<String> {
        return ArrayList(Plugins.LOADED_ECO_PLUGINS.keys)
    }

    override fun getPluginByName(name: String): EcoPlugin? {
        return Plugins.LOADED_ECO_PLUGINS[name.lowercase()]
    }

    override fun createFastItemStack(itemStack: ItemStack): FastItemStack {
        return getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)
    }

    override fun registerBStats(plugin: EcoPlugin) {
        val bStatsFolder = File(plugin.dataFolder.parentFile, "bStats")
        val configFile = File(bStatsFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)

        if (config.isSet("serverUuid")) {
            config.set("enabled", this.ecoPlugin.configYml.getBool("enable-bstats"))
            config.save(configFile)
        }

        Metrics(plugin, plugin.bStatsId)
    }
}