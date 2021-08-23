package com.willfp.eco.internal.config.json

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.LoadableConfig
import org.jetbrains.annotations.NotNull
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Suppress("UNCHECKED_CAST")
class EcoLoadableJSONConfig(
    configName: String,
    private val plugin: EcoPlugin,
    private val subDirectoryPath: String,
    private val source: Class<*>
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

    override fun createFile() {
        val inputStream = source.getResourceAsStream(resourcePath)!!
        val outFile = File(this.plugin.dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(this.plugin.dataFolder, resourcePath.substring(0, lastIndex.coerceAtLeast(0)))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        if (!outFile.exists()) {
            val out: OutputStream = FileOutputStream(outFile)
            inputStream.copyTo(out, 1024)
            out.close()
            inputStream.close()
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
        super.init(handle.fromJson(FileReader(file), Map::class.java) as @NotNull MutableMap<String, Any>)
    }

    override fun getName(): String {
        return name
    }

    override fun getConfigFile(): File {
        return configFile
    }

    init {
        val directory: File = File(this.plugin.dataFolder, subDirectoryPath)
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