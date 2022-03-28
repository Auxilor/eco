package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.interfaces.LoadableConfig
import com.willfp.eco.core.config.wrapper.ConfigFactory
import org.bukkit.configuration.ConfigurationSection

object EcoConfigFactory : ConfigFactory {
    override fun createConfig(bukkit: ConfigurationSection): Config {
        val config = createConfig(emptyMap(), ConfigType.YAML)
        for (key in bukkit.getKeys(true)) {
            config.set(key, bukkit.get(key))
        }

        return config
    }

    override fun createConfig(values: Map<String, Any>, type: ConfigType): Config =
        EcoConfigSection(type, values)

    override fun createConfig(contents: String, type: ConfigType): Config =
        EcoConfigSection(type, type.toMap(contents))

    override fun createLoadableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        type: ConfigType,
        requiresChangesToSave: Boolean
    ): LoadableConfig = EcoLoadableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source,
        requiresChangesToSave
    )

    override fun createUpdatableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        type: ConfigType,
        requiresChangesToSave: Boolean,
        vararg updateBlacklist: String
    ): LoadableConfig = EcoUpdatableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source,
        removeUnused,
        requiresChangesToSave,
        *updateBlacklist
    )
}
