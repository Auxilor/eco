package com.willfp.eco.spigot.integrations.bstats

import com.willfp.eco.core.EcoPlugin
import org.bstats.bukkit.Metrics
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object MetricHandler {
    fun createMetrics(plugin: EcoPlugin, ecoPlugin: EcoPlugin) {
        val bStatsFolder = File(plugin.dataFolder.parentFile, "bStats")
        val configFile = File(bStatsFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)

        if (config.isSet("serverUuid")) {
            config.set("enabled", ecoPlugin.configYml.getBool("enable-bstats"))
            config.save(configFile)
        }

        Metrics(plugin, plugin.bStatsId)
    }
}