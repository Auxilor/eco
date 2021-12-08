package com.willfp.eco.internal.config.json

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.interfaces.LoadableConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Suppress("UNCHECKED_CAST")
open class EcoLoadableJSONConfig(
    configName: String,
    private val plugin: PluginLike,
    private val subDirectoryPath: String,
    val source: Class<*>
) : EcoJSONConfigWrapper(), LoadableConfig {

    private val configFile: File
    private val name: String = "$configName.json"

    fun reloadFromFile() {
        try {
            init(configFile)
        } catch (e: IOException) {
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
        if (configFile.delete()) {
            Files.write(
                configFile.toPath(),
                toPlaintext().toByteArray(),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
            )
        }
    }

    @Throws(FileNotFoundException::class)
    fun init(file: File) {
        super.init(handle.fromJson(FileReader(file), Map::class.java) as MutableMap<String, Any>)
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
        try {
            init(configFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        plugin.configHandler.addConfig(this)
    }
}