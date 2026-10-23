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

        val target = generateSequence(1) { it + 1 }
            .map { number -> File(directory, if (number == 1) "$EXISTING.yml" else "$EXISTING-$number.yml") }
            .first { !it.exists() }

        if (target.name != "$EXISTING.yml") {
            logger.warning("lang/$EXISTING.yml already exists, so lang.yml was moved to lang/${target.name} instead")
        }

        return try {
            Files.move(legacy.toPath(), target.toPath())
            true
        } catch (e: IOException) {
            logger.warning(
                "Could not move lang.yml to lang/${target.name}, leaving it in place: ${e.message.orEmpty()}"
            )
            false
        }
    }

    /**
     * Copies out every shipped language, then fills each language file on disk with the keys
     * it is missing: from its own shipped copy, then from english. Keys are never removed.
     *
     * Language files are read and written directly rather than as configs: only the selected
     * language is registered with the plugin, or every reload and save would touch every language.
     * Their leading comment lines are kept the same way [EcoLoadableConfig] keeps them.
     */
    fun sync(plugin: PluginLike, source: LangSource) {
        val directory = File(plugin.dataFolder, DIRECTORY)
        directory.mkdirs()

        val shipped = readShipped(plugin, source)
        val english = shipped[ENGLISH]?.config

        for ((language, shippedLanguage) in shipped) {
            val file = File(directory, "$language.yml")
            if (!file.exists()) {
                file.writeText(shippedLanguage.text, Charsets.UTF_8)
            }
        }

        for (file in languageFiles(directory)) {
            fillLanguageFile(plugin, file, shipped[file.nameWithoutExtension.lowercase()]?.config, english)
        }

        if (english != null) {
            warnIncompleteShipped(plugin, shipped, english)
        }
    }

    /**
     * Loads the requested language, or english if the plugin doesn't have it.
     *
     * @param plugin The plugin to load the language for.
     * @param requested The language set in eco's config.yml.
     * @param migrated If this plugin's lang.yml was just moved to lang/existing.yml.
     */
    fun select(plugin: PluginLike, requested: String?, migrated: Boolean): EcoLoadableConfig {
        val directory = File(plugin.dataFolder, DIRECTORY)
        directory.mkdirs()

        val language = requested?.run { trim().lowercase().ifEmpty { null } } ?: ENGLISH
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

    private class ShippedLanguage(val text: String, val config: Config)

    private fun readShipped(plugin: PluginLike, source: LangSource): Map<String, ShippedLanguage> {
        val shipped = mutableMapOf<String, ShippedLanguage>()

        for (language in source.languages) {
            val text = source.read(language).orEmpty()
            runCatching { parse(text) }
                .onSuccess { config -> shipped[language] = ShippedLanguage(text, config) }
                .onFailure { error ->
                    plugin.logger.warning(
                        "The jar's $language language could not be read, skipping it: ${error.message.orEmpty()}"
                    )
                }
        }

        return shipped
    }

    private fun fillLanguageFile(plugin: PluginLike, file: File, shipped: Config?, english: Config?) {
        val contents = file.readText(Charsets.UTF_8)
        val config = runCatching { parse(contents) }
            .onFailure { error ->
                plugin.logger.warning("Could not read lang/${file.name}, skipping it: ${error.message.orEmpty()}")
            }
            .getOrNull()
            ?: return

        val added = shipped?.let { addMissingKeys(to = config, from = it) }.orEmpty()

        val addedFromEnglish = when {
            english == null -> emptyList()
            file.nameWithoutExtension.equals(ENGLISH, ignoreCase = true) -> emptyList()
            else -> addMissingKeys(to = config, from = english)
        }

        if (addedFromEnglish.isNotEmpty()) {
            plugin.logger.warning(
                "Added english messages to lang/${file.name} for missing keys: ${addedFromEnglish.joinToString()}"
            )
        }

        if (added.isNotEmpty() || addedFromEnglish.isNotEmpty()) {
            file.writeText(leadingComments(contents) + config.toPlaintext(), Charsets.UTF_8)
        }
    }

    private fun warnIncompleteShipped(plugin: PluginLike, shipped: Map<String, ShippedLanguage>, english: Config) {
        for ((language, shippedLanguage) in shipped.filterKeys { it != ENGLISH }) {
            val missing = english.getKeys(true).filterNot { shippedLanguage.config.has(it) }
            if (missing.isNotEmpty()) {
                plugin.logger.warning(
                    "The jar's $language language is missing keys that english has: ${missing.joinToString()}"
                )
            }
        }
    }

    private fun languageFiles(directory: File): List<File> =
        directory.listFiles { file -> file.isFile && file.extension == "yml" }?.sortedBy { it.name }.orEmpty()

    private fun load(plugin: PluginLike, language: String) =
        EcoLoadableConfig(ConfigType.YAML, language, plugin, "$DIRECTORY/", LangFiles::class.java, true)

    private fun leadingComments(contents: String): String {
        val header = contents.lines().takeWhile { it.startsWith("#") }
        return if (header.isEmpty()) "" else header.joinToString("\n", postfix = "\n\n")
    }

    private fun parse(text: String?): Config =
        EcoConfig(ConfigType.YAML).apply { init(ConfigType.YAML.toMap(text), emptyMap()) }

    private fun addMissingKeys(to: Config, from: Config): List<String> {
        val added = mutableListOf<String>()

        for (key in from.getKeys(true)) {
            if (!to.has(key)) {
                to[key] = from[key]
                added.add(key)
            }
        }

        return added
    }
}
