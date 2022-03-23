package com.willfp.eco.internal.config

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.LoadableConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.nio.file.Files
import java.nio.file.StandardOpenOption

@Suppress("UNCHECKED_CAST")
open class EcoLoadableConfig(
    type: ConfigType,
    configName: String,
    private val plugin: PluginLike,
    private val subDirectoryPath: String,
    val source: Class<*>
) : EcoConfig(type), LoadableConfig {
    private val configFile: File
    private val name: String = "$configName.${type.extension}"

    fun reloadFromFile() {
        runCatching { init(configFile) }.onFailure { it.printStackTrace() }
    }

    final override fun createFile() {
        val inputStream = source.getResourceAsStream(resourcePath)!!
        val outFile = File(this.plugin.dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(this.plugin.dataFolder, resourcePath.substring(0, lastIndex.coerceAtLeast(0)))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        if (!outFile.exists()) {
            val out: OutputStream = FileOutputStream(outFile)
            inputStream.copyTo(out)
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

    protected fun init(reader: Reader) {
        super.init(type.handler.toMap(reader.readToString()))
    }

    fun init(file: File) {
        init(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
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
        init(configFile)
        plugin.configHandler.addConfig(this)
    }
}
