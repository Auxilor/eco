package com.willfp.eco.internal

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginProps
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.readConfig

object EcoPropsParser : PluginProps.PropsParser<Config> {
    override fun parseFrom(config: Config): PluginProps {
        val resourceId = config.getIntOrNull("resource-id") ?: 0
        val bStatsId = config.getIntOrNull("bstats-id") ?: 0
        val proxyPackage = config.getStringOrNull("proxy-package") ?: ""
        val color = config.getStringOrNull("color") ?: "&f"
        val supportsExtensions = config.getBoolOrNull("supports-extensions") ?: false

        return blankProps.apply {
            this.resourceId = resourceId
            this.bStatsId = bStatsId
            this.proxyPackage = proxyPackage
            this.color = color
            this.isSupportingExtensions = supportsExtensions
        }
    }

    fun parseForPlugin(plugin: Class<out EcoPlugin>): PluginProps {
        if (!PluginProps.hasParserFor(Config::class.java)) {
            PluginProps.registerParser(Config::class.java, this)
        }

        return PluginProps.parse(
            plugin.getResourceAsStream("/eco.yml").readConfig(),
            Config::class.java
        )
    }
}
