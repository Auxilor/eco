package com.willfp.eco.internal

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.proxy.Cleaner
import com.willfp.eco.internal.proxy.EcoProxyFactory
import java.net.URLClassLoader

class EcoCleaner: Cleaner {
    override fun clean(plugin: EcoPlugin) {
        if (plugin.proxyPackage.isNotEmpty()) {
            val factory = plugin.proxyFactory as EcoProxyFactory
            factory.clean()
        }

        Plugins.LOADED_ECO_PLUGINS.remove(plugin.name.lowercase())

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
}