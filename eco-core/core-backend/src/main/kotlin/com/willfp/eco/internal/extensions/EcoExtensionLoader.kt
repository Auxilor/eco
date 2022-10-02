package com.willfp.eco.internal.extensions

import com.google.common.collect.ImmutableSet
import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.TransientConfig
import com.willfp.eco.core.extensions.Extension
import com.willfp.eco.core.extensions.ExtensionLoader
import com.willfp.eco.core.extensions.ExtensionMetadata
import com.willfp.eco.core.extensions.MalformedExtensionException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.net.URLClassLoader

class EcoExtensionLoader(
    private val plugin: EcoPlugin
) : ExtensionLoader {
    private val extensions = mutableMapOf<Extension, URLClassLoader>()

    override fun loadExtensions() {
        val dir = File(this.plugin.dataFolder, "/extensions")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val extensionJars = dir.listFiles() ?: return

        for (extensionJar in extensionJars) {
            if (!extensionJar.isFile) {
                continue
            }

            runCatching { loadExtension(extensionJar) }.onFailure {
                this.plugin.logger.warning(extensionJar.name + " caused an error!")
                if (Eco.get().ecoPlugin.configYml.getBool("log-full-extension-errors")) {
                    it.printStackTrace()
                }
            }
        }
    }

    @Throws(MalformedExtensionException::class)
    private fun loadExtension(extensionJar: File) {
        val url = extensionJar.toURI().toURL()

        val classLoader = URLClassLoader(arrayOf(url), this.plugin::class.java.classLoader)
        val ymlIn = classLoader.getResourceAsStream("extension.yml")
            ?: throw MalformedExtensionException("No extension.yml found in " + extensionJar.name)

        val extensionYml = TransientConfig(YamlConfiguration.loadConfiguration(InputStreamReader(ymlIn)))

        val mainClass = extensionYml.getStringOrNull("main")
        var name = extensionYml.getStringOrNull("name")
        var version = extensionYml.getStringOrNull("version")
        var author = extensionYml.getStringOrNull("author")


        if (mainClass == null) {
            throw MalformedExtensionException("Invalid extension.yml found in " + extensionJar.name)
        }

        if (name == null) {
            this.plugin.logger.warning(extensionJar.name + " doesn't have a name!")
            name = "Unnamed Extension " + extensionJar.name
        }

        if (version == null) {
            this.plugin.logger.warning(extensionJar.name + " doesn't have a version!")
            version = "1.0.0"
        }

        if (author == null) {
            this.plugin.logger.warning(extensionJar.name + " doesn't have an author!")
            author = "Unnamed Author"
        }

        val metadata = ExtensionMetadata(version, name, author)

        val cls: Class<*> = classLoader.loadClass(mainClass)
        val extension: Extension = cls.getConstructor(EcoPlugin::class.java).newInstance(this.plugin) as Extension

        extension.setMetadata(metadata)
        extension.enable()
        extensions[extension] = classLoader
    }

    override fun getLoadedExtensions(): MutableSet<Extension> {
        return ImmutableSet.copyOf(extensions.keys)
    }

    override fun unloadExtensions() {
        extensions.keys.forEach { e -> e.disable() }
        for (urlClassLoader in extensions.values) {
            urlClassLoader.close()
        }

        extensions.clear()
    }
}