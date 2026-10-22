package com.willfp.eco.internal.config.lang

import com.willfp.eco.core.PluginLike
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.internal.config.EcoConfig
import com.willfp.eco.internal.config.EcoLoadableConfig
import com.willfp.eco.internal.config.toMap
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.logging.Logger

/**
 * The language files under plugins/<plugin>/lang/.
 */
object LangFiles {
    const val DIRECTORY = "lang"
    const val ENGLISH = "english"
    const val EXISTING = "existing"

    /**
     * Moves a lang.yml from before languages existed into lang/, so it stays selectable as
     * "existing". An existing.yml already there is never overwritten.
     *
     * @return If a lang.yml was moved.
     */
    fun migrate(dataFolder: File, logger: Logger): Boolean {
        val legacy = File(dataFolder, "lang.yml")
        if (!legacy.isFile) {
            return false
        }

        val directory = File(dataFolder, DIRECTORY)
        directory.mkdirs()

        var target = File(directory, "$EXISTING.yml")
        if (target.exists()) {
            var n = 2
            while (File(directory, "$EXISTING-$n.yml").exists()) {
                n++
            }
            target = File(directory, "$EXISTING-$n.yml")

            logger.warning(
                "lang/$EXISTING.yml already exists, so lang.yml was moved to lang/${target.name} instead"
            )
        }

        try {
            Files.move(legacy.toPath(), target.toPath())
        } catch (e: IOException) {
            logger.warning("Could not move lang.yml to lang/${target.name}, leaving it in place: ${e.message}")
            return false
        }

        return true
    }

    /**
     * Copies out every shipped language, then fills each language file on disk with the keys
     * it is missing: from its own shipped copy, then from english. Keys are never removed.
     */
    fun sync(plugin: PluginLike, source: LangSource) {
        val directory = File(plugin.dataFolder, DIRECTORY)
        directory.mkdirs()

        val texts = mutableMapOf<String, String>()
        val shipped = mutableMapOf<String, Config>()
        for (language in source.languages) {
            val text = source.read(language) ?: ""
            shipped[language] = runCatching { parse(text) }.getOrElse {
                plugin.logger.warning("The jar's $language language could not be read, skipping it: ${it.message}")
                continue
            }
            texts[language] = text
        }
        val english = shipped[ENGLISH]

        for ((language, text) in texts) {
            val file = File(directory, "$language.yml")
            if (!file.exists()) {
                file.writeText(text, Charsets.UTF_8)
            }
        }

        // Read and written directly rather than as configs: only the selected language is
        // registered with the plugin, or every reload and save would touch every language.
        for (file in languageFiles(directory)) {
            val contents = file.readText(Charsets.UTF_8)
            val config = runCatching { parse(contents) }.getOrElse {
                plugin.logger.warning("Could not read lang/${file.name}, skipping it: ${it.message}")
                continue
            }

            val added = shipped[file.nameWithoutExtension.lowercase()]?.let { fill(config, it) }.orEmpty()

            val addedFromEnglish = if (english != null && !file.nameWithoutExtension.equals(ENGLISH, ignoreCase = true)) {
                fill(config, english)
            } else {
                emptyList()
            }

            if (addedFromEnglish.isNotEmpty()) {
                plugin.logger.warning(
                    "Added english messages to lang/${file.name} for missing keys: ${addedFromEnglish.joinToString()}"
                )
            }

            if (added.isNotEmpty() || addedFromEnglish.isNotEmpty()) {
                file.writeText(header(contents) + config.toPlaintext(), Charsets.UTF_8)
            }
        }

        if (english != null) {
            for ((language, config) in shipped) {
                if (language == ENGLISH) {
                    continue
                }

                val missing = english.getKeys(true).filterNot { config.has(it) }
                if (missing.isNotEmpty()) {
                    plugin.logger.warning(
                        "The jar's $language language is missing keys that english has: ${missing.joinToString()}"
                    )
                }
            }
        }
    }

    /**
     * Loads the requested language, or english if the plugin doesn't have it.
     *
     * @param requested The language set in eco's config.yml.
     * @param migrated If this plugin's lang.yml was just moved to lang/existing.yml.
     */
    fun select(plugin: PluginLike, requested: String?, migrated: Boolean): EcoLoadableConfig {
        val directory = File(plugin.dataFolder, DIRECTORY)
        directory.mkdirs()

        val language = requested?.trim()?.lowercase()?.ifEmpty { null } ?: ENGLISH
        val files = languageFiles(directory).associateBy { it.nameWithoutExtension.lowercase() }

        if (migrated && language != EXISTING) {
            plugin.logger.warning(
                "Your old lang.yml was moved to lang/$EXISTING.yml. Set language: $EXISTING " +
                        "in eco's config.yml and restart to use it again."
            )
        }

        val selected = files[language]
        if (selected != null) {
            val config = runCatching { load(plugin, selected.nameWithoutExtension) }.getOrNull()
            if (config != null) {
                return config
            }
        }

        if (language != ENGLISH) {
            plugin.logger.warning(
                "Language '$language' is not available or could not be read, using $ENGLISH instead"
            )
        }

        val english = files[ENGLISH] ?: File(directory, "$ENGLISH.yml").apply { createNewFile() }
        return load(plugin, english.nameWithoutExtension)
    }

    private fun languageFiles(directory: File): List<File> =
        directory.listFiles { file -> file.isFile && file.extension == "yml" }?.sortedBy { it.name }
            ?: emptyList()

    private fun load(plugin: PluginLike, language: String) =
        EcoLoadableConfig(ConfigType.YAML, language, plugin, "$DIRECTORY/", LangFiles::class.java, true)

    /**
     * The leading comment lines of a file, kept the same way [EcoLoadableConfig] keeps them.
     */
    private fun header(contents: String): String {
        val header = contents.lines().takeWhile { it.startsWith("#") }
        return if (header.isEmpty()) "" else header.joinToString("\n", postfix = "\n\n")
    }

    private fun parse(text: String?): Config =
        EcoConfig(ConfigType.YAML).apply { init(ConfigType.YAML.toMap(text), emptyMap()) }

    /**
     * Adds every key [from] has that [to] doesn't.
     *
     * @return The keys that were added.
     */
    private fun fill(to: Config, from: Config): List<String> {
        val added = mutableListOf<String>()

        for (key in from.getKeys(true)) {
            if (!to.has(key)) {
                to.set(key, from.get(key))
                added.add(key)
            }
        }

        return added
    }
}
