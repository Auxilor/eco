package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

open class EcoUpdatableConfig(
    type: ConfigType,
    configName: String,
    plugin: PluginLike,
    subDirectoryPath: String,
    source: Class<*>,
    private val removeUnused: Boolean,
    requiresChangesToSave: Boolean,
    vararg updateBlacklist: String
) : EcoLoadableConfig(type, configName, plugin, subDirectoryPath, source, requiresChangesToSave) {
    private val updateBlacklist = mutableListOf(*updateBlacklist)

    fun update() {
        this.init(configFile)
        val newConfig = configInJar ?: return
        if (newConfig.getKeys(true) == this.getKeys(true)) {
            return
        }
        newConfig.getKeys(true).forEach { key: String ->
            if (!this.getKeys(true).contains(key)) {
                if (updateBlacklist.stream().noneMatch { s: String -> key.contains(s) }) {
                    this.set(key, newConfig[key])
                }
            }
        }
        if (removeUnused) {
            this.getKeys(true).forEach { s ->
                if (!newConfig.getKeys(true).contains(s)) {
                    if (updateBlacklist.stream().noneMatch(s::contains)) {
                        this.set(s, null)
                    }
                }
            }
        }
        this.save()
    }

    private val configInJar: Config?
        get() {
            val newIn = this.source.getResourceAsStream(resourcePath) ?: return null
            val reader = BufferedReader(InputStreamReader(newIn, StandardCharsets.UTF_8))

            val config = EcoConfigSection(type, emptyMap())
            config.init(type.toMap(reader.readToString()), emptyMap())
            return config
        }

    init {
        this.updateBlacklist.removeIf { obj: String -> obj.isEmpty() }
        postInit(plugin)
    }

    private fun postInit(plugin: PluginLike) {
        plugin.configHandler.addConfig(this)
        update()
    }
}
