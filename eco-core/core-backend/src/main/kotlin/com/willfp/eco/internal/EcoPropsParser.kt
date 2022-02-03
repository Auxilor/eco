package com.willfp.eco.internal

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.EcoPluginProps
import com.willfp.eco.core.config.TransientConfig
import com.willfp.eco.core.config.interfaces.Config
import org.objenesis.ObjenesisStd

object EcoPropsParser : EcoPluginProps.PropsParser<Config> {
    private val constructor = ObjenesisStd().getInstantiatorOf(EcoPluginProps::class.java);

    override fun parseFrom(config: Config): EcoPluginProps {
        val resourceId = config.getIntOrNull("resource-id") ?: 0
        val bStatsId = config.getIntOrNull("bstats-id") ?: 0
        val proxyPackage = config.getStringOrNull("proxy-package") ?: ""
        val color = config.getStringOrNull("color") ?: "&f"
        val supportsExtensions = config.getBoolOrNull("supports-extensions") ?: false

        return constructor.newInstance().apply {
            this.resourceId = resourceId
            this.bStatsId = bStatsId
            this.proxyPackage = proxyPackage
            this.color = color
            this.isSupportingExtensions = supportsExtensions
        }
    }

    fun parseForPlugin(plugin: Class<out EcoPlugin>): EcoPluginProps {
        if (!EcoPluginProps.hasParserFor(Config::class.java)) {
            EcoPluginProps.registerParser(Config::class.java, this)
        }

        return EcoPluginProps.parse(
            TransientConfig(plugin.getResourceAsStream("/eco.yml")),
            Config::class.java
        )
    }
}
