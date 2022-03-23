package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.interfaces.LoadableConfig
import com.willfp.eco.core.config.wrapper.ConfigFactory
import org.bukkit.configuration.ConfigurationSection
import java.io.BufferedReader
import java.io.Reader

object EcoConfigFactory : ConfigFactory {
    override fun createConfig(config: ConfigurationSection): Config =
        createConfig(config.getValues(true), ConfigType.YAML)

    override fun createConfig(values: Map<String, Any>, type: ConfigType): Config =
        EcoConfigSection(type, values)

    override fun createConfig(contents: String, type: ConfigType): Config =
        EcoConfigSection(type, type.toMap(contents))

    override fun createLoadableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        type: ConfigType
    ): LoadableConfig = EcoLoadableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source
    )

    override fun createUpdatableConfig(
        configName: String,
        plugin: PluginLike,
        subDirectoryPath: String,
        source: Class<*>,
        removeUnused: Boolean,
        type: ConfigType,
        vararg updateBlacklist: String
    ): LoadableConfig = EcoUpdatableConfig(
        type,
        configName,
        plugin,
        subDirectoryPath,
        source,
        removeUnused,
        *updateBlacklist
    )
}

fun Reader.readToString(): String {
    val input = this as? BufferedReader ?: BufferedReader(this)
    val builder = StringBuilder()

    var line: String?
    input.use {
        while (it.readLine().also { read -> line = read } != null) {
            builder.append(line)
            builder.append('\n')
        }
    }

    return builder.toString()
}