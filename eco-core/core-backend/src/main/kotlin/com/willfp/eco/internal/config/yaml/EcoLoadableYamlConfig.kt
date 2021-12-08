package com.willfp.eco.internal.config.yaml

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.interfaces.LoadableConfig
import com.willfp.eco.core.config.interfaces.WrappedYamlConfiguration
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

open class EcoLoadableYamlConfig(
    configName: String,
    private val plugin: PluginLike,
    private val subDirectoryPath: String,
    val source: Class<*>
) : EcoYamlConfigWrapper<YamlConfiguration>(), WrappedYamlConfiguration, LoadableConfig {

    private val configFile: File
    private val name: String = "$configName.yml"

    fun reloadFromFile() {
        try {
            handle.load(getConfigFile())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    final override fun createFile() {
        val inputFile = File(source.getResource(resourcePath)!!.path)
        val outFile = File(this.plugin.dataFolder, resourcePath)
        if (!outFile.exists()) {
            inputFile.copyTo(outFile, true, 1024)
        }
    }

    override fun getResourcePath(): String {
        val resourcePath: String = if (subDirectoryPath.isEmpty()) {
            name
        } else {
            subDirectoryPath + name
        }
        return "/$resourcePath"
    }

    @Throws(IOException::class)
    override fun save() {
        handle.save(getConfigFile())
    }

    override fun getBukkitHandle(): YamlConfiguration {
        return handle
    }

    override fun getName(): String {
        return name
    }

    override fun getConfigFile(): File {
        return configFile
    }

    init {
        val directory = File(this.plugin.dataFolder, subDirectoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        if (!File(directory, name).exists()) {
            createFile()
        }
        configFile = File(directory, name)
        this.plugin.configHandler.addConfig(this)
        init(YamlConfiguration.loadConfiguration(configFile))
    }
}