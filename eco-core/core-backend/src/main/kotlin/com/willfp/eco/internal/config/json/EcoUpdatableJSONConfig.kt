package com.willfp.eco.internal.config.json

import com.willfp.eco.core.PluginLike
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

open class EcoUpdatableJSONConfig(
    configName: String,
    plugin: PluginLike,
    subDirectoryPath: String,
    source: Class<*>,
    private val removeUnused: Boolean,
    vararg updateBlacklist: String
) : EcoLoadableJSONConfig(configName, plugin, subDirectoryPath, source) {

    private val updateBlacklist: MutableList<String> = mutableListOf(*updateBlacklist)

    fun update() {
        super.clearCache()
        try {
            this.init(configFile)
            val newConfig = configInJar
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
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    private val configInJar: YamlConfiguration
        get() {
            val newIn = this.source.getResourceAsStream(resourcePath) ?: throw NullPointerException("$name is null?")
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