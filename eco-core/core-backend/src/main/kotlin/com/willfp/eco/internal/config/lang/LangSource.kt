package com.willfp.eco.internal.config.lang

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.logging.Logger

/**
 * The language files a plugin ships.
 */
interface LangSource {
    /**
     * Lowercase language names the plugin ships, e.g. "english", "spanish".
     */
    val languages: Set<String>

    /**
     * The contents of a language's shipped file, or null if it isn't shipped.
     */
    fun read(language: String): String?
}

/**
 * Reads lang/<name>.yml entries from a plugin jar.
 *
 * A root lang.yml is what plugins shipped before languages existed, so it counts as english
 * unless the jar also ships lang/english.yml.
 */
class JarLangSource(jar: File) : LangSource {
    private val files: Map<String, String> = JarFile(jar).use { readLanguages(it) }

    override val languages: Set<String>
        get() = files.keys

    override fun read(language: String): String? = files[language.lowercase()]

    companion object {
        /**
         * Reads a plugin's jar, or ships nothing if it can't be read as one, so a plugin loaded
         * some other way still comes up.
         */
        fun open(jar: File, logger: Logger): LangSource = runCatching { JarLangSource(jar) }.getOrElse { error ->
            logger.warning("Could not read language files from ${jar.name}: ${error.message.orEmpty()}")
            EmptyLangSource
        }

        private fun readLanguages(jarFile: JarFile): Map<String, String> {
            val files = mutableMapOf<String, String>()
            var legacyEnglish: String? = null

            for (entry in jarFile.entries()) {
                if (!entry.isDirectory && entry.name == "lang.yml") {
                    legacyEnglish = jarFile.readText(entry)
                } else {
                    languageOf(entry)?.let { files[it] = jarFile.readText(entry) }
                }
            }

            legacyEnglish?.let { files.putIfAbsent(LangFiles.ENGLISH, it) }
            return files
        }

        private fun languageOf(entry: JarEntry): String? {
            val name = entry.name
            if (entry.isDirectory || !name.startsWith("lang/") || !name.endsWith(".yml")) {
                return null
            }

            val language = name.removePrefix("lang/").removeSuffix(".yml")
            return language.lowercase().takeIf { language.isNotEmpty() && '/' !in language }
        }

        private fun JarFile.readText(entry: JarEntry): String =
            getInputStream(entry).use { it.readBytes().toString(Charsets.UTF_8) }
    }
}

private object EmptyLangSource : LangSource {
    override val languages: Set<String> = emptySet()

    override fun read(language: String): String? = null
}
