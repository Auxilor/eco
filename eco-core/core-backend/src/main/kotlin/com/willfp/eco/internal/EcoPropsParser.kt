package com.willfp.eco.internal

import com.willfp.eco.core.EcoPluginProps
import com.willfp.eco.core.config.interfaces.Config

class EcoPropsParser : EcoPluginProps.PropsParser<Config> {
    override fun parseFrom(config: Config): EcoPluginProps {
        val resourceId = config.getIntOrNull("resource-id") ?: 0
        val bstatsId = config.getIntOrNull("bstats-id") ?: 0
        val proxyPackage = config.getStringOrNull("proxy-package") ?: ""
        val color = config.getStringOrNull("color") ?: "&f"
        val supportsExtensions = config.getBoolOrNull("supports-extensions") ?: false

        return EcoPluginProps(
            resourceId,
            bstatsId,
            proxyPackage,
            color,
            supportsExtensions
        )
    }
}