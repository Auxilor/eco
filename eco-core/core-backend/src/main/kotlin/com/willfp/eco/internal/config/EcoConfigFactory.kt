package com.willfp.eco.internal.config

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.config.wrapper.ConfigFactory
import com.willfp.eco.internal.config.json.EcoJSONConfigSection
import com.willfp.eco.internal.config.json.EcoLoadableJSONConfig
import com.willfp.eco.internal.config.json.EcoUpdatableJSONConfig
import com.willfp.eco.internal.config.yaml.EcoLoadableYamlConfig
import com.willfp.eco.internal.config.yaml.EcoUpdatableYamlConfig
import com.willfp.eco.internal.config.yaml.EcoYamlConfigSection
import org.bukkit.configuration.file.YamlConfiguration

class EcoConfigFactory : ConfigFactory {
    override fun createUpdatableYamlConfig(
        configName: String,
        plugin: EcoPlugin,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        vararg updateBlacklist: String
    ): Config {
        return EcoUpdatableYamlConfig(
            configName,
            plugin,
            subDirectoryPath,
            source,
            removeUnused,
            *updateBlacklist
        )
    }

    override fun createUpdatableJSONConfig(
        configName: String,
        plugin: EcoPlugin,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        vararg updateBlacklist: String
    ): JSONConfig {
        return EcoUpdatableJSONConfig(
            configName,
            plugin,
            subDirectoryPath,
            source,
            removeUnused,
            *updateBlacklist
        )
    }

    override fun createLoadableJSONConfig(
        configName: String,
        plugin: EcoPlugin,
        subDirectoryPath: String,
        source: Class<*>
    ): JSONConfig {
        return EcoLoadableJSONConfig(
            configName,
            plugin,
            subDirectoryPath,
            source
        )
    }

    override fun createLoadableYamlConfig(
        configName: String,
        plugin: EcoPlugin,
        subDirectoryPath: String,
        source: Class<*>
    ): Config {
        return EcoLoadableYamlConfig(
            configName,
            plugin,
            subDirectoryPath,
            source
        )
    }

    override fun createYamlConfig(config: YamlConfiguration): Config {
        return EcoYamlConfigSection(config)
    }

    override fun createJSONConfig(values: Map<String, Any>): JSONConfig {
        return EcoJSONConfigSection(values)
    }
}