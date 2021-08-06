package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.EcoPlugin
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class EcoUpdatableYamlConfig(
    configName: String,
    plugin: EcoPlugin,
    subDirectoryPath: String,
    source: Class<*>,
    private val removeUnused: Boolean,
    vararg updateBlacklist: String
) : EcoLoadableYamlConfig(configName, plugin, subDirectoryPath, source) {

    private val updateBlacklist: MutableList<String> = mutableListOf(*updateBlacklist)

    fun update() {
        super.clearCache()
        try {
            this.handle.load(configFile)
            val newConfig = configInJar
            if (newConfig.getKeys(true) == this.handle.getKeys(true)) {
                return
            }
            newConfig.getKeys(true).forEach { key: String ->
                if (!this.handle.getKeys(true).contains(key)) {
                    if (updateBlacklist.stream().noneMatch { s: String -> key.contains(s) }) {
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
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    private val configInJar: YamlConfiguration
        get() {
            val newIn = source.getResourceAsStream(resourcePath) ?: throw NullPointerException("$name is null?")
            val reader = BufferedReader(InputStreamReader(newIn, StandardCharsets.UTF_8))
            val newConfig = YamlConfiguration()
            try {
                newConfig.load(reader)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InvalidConfigurationException) {
                e.printStackTrace()
            }
            return newConfig
        }

    init {
        this.updateBlacklist.removeIf { obj: String -> obj.isEmpty() }
        plugin.configHandler.addConfig(this)
        update()
    }
}