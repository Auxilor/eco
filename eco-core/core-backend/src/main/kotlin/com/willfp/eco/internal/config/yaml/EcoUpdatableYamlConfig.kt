package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.PluginLike
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class EcoUpdatableYamlConfig(
    configName: String,
    plugin: PluginLike,
    subDirectoryPath: String,
    source: Class<*>,
    private val removeUnused: Boolean,
    vararg updateBlacklist: String
) : EcoLoadableYamlConfig(configName, plugin, subDirectoryPath, source) {
    private val updateBlacklist: MutableList<String> = mutableListOf(*updateBlacklist)

    fun update() {
        super.clearCache()
        this.handle.load(configFile)
        val newConfig = configInJar ?: return
        if (newConfig.getKeys(true) == this.handle.getKeys(true)) {
            return
        }
        newConfig.getKeys(true).forEach { key ->
            if (!this.handle.getKeys(true).contains(key)) {
                if (updateBlacklist.stream().noneMatch { key.contains(it) }) {
                    this.handle.set(key, newConfig[key])
                }
            }
        }
        if (removeUnused) {
            this.handle.getKeys(true).forEach { s ->
                if (!newConfig.getKeys(true).contains(s)) {
                    if (updateBlacklist.stream().noneMatch(s::contains)) {
                        this.handle.set(s, null)
                    }
                }
            }
        }
        this.handle.save(configFile)
    }

    private val configInJar: YamlConfiguration?
        get() {
            val newIn = source.getResourceAsStream(resourcePath) ?: return null
            val reader = BufferedReader(InputStreamReader(newIn, StandardCharsets.UTF_8))
            val newConfig = YamlConfiguration()
            newConfig.load(reader)
            return newConfig
        }

    init {
        this.updateBlacklist.removeIf { it.isEmpty() }
        plugin.configHandler.addConfig(this)
        update()
    }
}