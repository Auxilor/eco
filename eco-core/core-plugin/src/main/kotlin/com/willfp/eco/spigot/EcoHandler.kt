package com.willfp.eco.spigot

import com.willfp.eco.core.EcoPlugin
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
import org.bukkit.inventory.ItemStack
import java.util.logging.Logger

class EcoHandler : EcoSpigotPlugin() {
    private var cleaner: Cleaner? = null

    fun createScheduler(plugin: EcoPlugin): Scheduler {
        return EcoScheduler(plugin)
    }

    fun createEventManager(plugin: EcoPlugin): EventManager {
        return EcoEventManager(plugin)
    }

    fun createNamespacedKeyFactory(plugin: EcoPlugin): NamespacedKeyFactory {
        return EcoNamespacedKeyFactory(plugin)
    }

    fun createMetadataValueFactory(plugin: EcoPlugin): MetadataValueFactory {
        return EcoMetadataValueFactory(plugin)
    }

    fun createRunnableFactory(plugin: EcoPlugin): RunnableFactory {
        return EcoRunnableFactory(plugin)
    }

    fun createExtensionLoader(plugin: EcoPlugin): ExtensionLoader {
        return EcoExtensionLoader(plugin)
    }

    fun createConfigHandler(plugin: EcoPlugin): ConfigHandler {
        return EcoConfigHandler(plugin)
    }

    fun createLogger(plugin: EcoPlugin): Logger {
        return EcoLogger(plugin)
    }

    fun createPAPIIntegration(plugin: EcoPlugin): PlaceholderIntegration {
        return PlaceholderIntegrationPAPI(plugin)
    }

    fun getEcoPlugin(): EcoPlugin {
        return this
    }

    fun getConfigFactory(): ConfigFactory {
        return EcoConfigFactory()
    }

    fun getDropQueueFactory(): DropQueueFactory {
        return EcoDropQueueFactory()
    }

    fun getGUIFactory(): GUIFactory {
        return EcoGUIFactory()
    }

    fun getCleaner(): Cleaner {
        if (cleaner == null) {
            cleaner = EcoCleaner()
        }
        return cleaner as Cleaner
    }

    fun createProxyFactory(plugin: EcoPlugin): ProxyFactory {
        return EcoProxyFactory(plugin)
    }

    fun addNewPlugin(plugin: EcoPlugin) {
        Plugins.LOADED_ECO_PLUGINS[plugin.name.lowercase()] = plugin
    }

    fun getLoadedPlugins(): List<String> {
        return ArrayList(Plugins.LOADED_ECO_PLUGINS.keys)
    }

    fun getPluginByName(name: String): EcoPlugin? {
        return Plugins.LOADED_ECO_PLUGINS[name.lowercase()]
    }

    fun createFastItemStack(itemStack: ItemStack): FastItemStack {
        return getProxy(FastItemStackFactoryProxy::class.java).create(itemStack)
    }
}