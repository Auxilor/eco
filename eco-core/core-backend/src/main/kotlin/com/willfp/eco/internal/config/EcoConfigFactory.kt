package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.interfaces.LoadableConfig
import com.willfp.eco.core.config.wrapper.ConfigFactory
import com.willfp.eco.internal.config.json.EcoJSONConfigSection
import com.willfp.eco.internal.config.json.EcoJSONConfigWrapper
import com.willfp.eco.internal.config.json.EcoLoadableJSONConfig
import com.willfp.eco.internal.config.json.EcoUpdatableJSONConfig
import com.willfp.eco.internal.config.yaml.EcoLoadableYamlConfig
import com.willfp.eco.internal.config.yaml.EcoUpdatableYamlConfig
import com.willfp.eco.internal.config.yaml.EcoYamlConfigSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.StringReader

class EcoConfigFactory : ConfigFactory {
    override fun createConfig(config: YamlConfiguration): Config {
        return EcoYamlConfigSection(config)
    }

    override fun createConfig(values: MutableMap<String, Any>): Config {
        return EcoJSONConfigSection(values)
    }

    override fun createConfig(contents: String, type: ConfigType): Config {
        return if (type == ConfigType.JSON) {
            @Suppress("UNCHECKED_CAST")
            EcoJSONConfigSection(
                EcoJSONConfigWrapper.gson.fromJson(
                    StringReader(contents), Map::class.java
                ) as MutableMap<String, Any>
            )
        } else {
            EcoYamlConfigSection(YamlConfiguration.loadConfiguration(StringReader(contents)))
        }
    }

    override fun createLoadableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        type: ConfigType
    ): LoadableConfig {
        return if (type == ConfigType.JSON) {
            EcoLoadableJSONConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
            )
        } else {
            EcoLoadableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
            )
        }
    }

    override fun createUpdatableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        type: ConfigType,
        vararg updateBlacklist: String
    ): LoadableConfig {
        return if (type == ConfigType.JSON) {
            EcoUpdatableJSONConfig(
                configName,
                plugin,
                subDirectoryPath,
                source,
                removeUnused,
                *updateBlacklist
            )
        } else {
            EcoUpdatableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source,
                removeUnused,
                *updateBlacklist
            )
        }
    }
}