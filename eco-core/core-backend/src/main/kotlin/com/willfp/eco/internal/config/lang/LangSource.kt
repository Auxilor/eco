package com.willfp.eco.internal.config.lang

import java.io.File
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
    private val files = mutableMapOf<String, String>()

    override val languages: Set<String>
        get() = files.keys

    override fun read(language: String): String? = files[language.lowercase()]

    init {
        var legacyEnglish: String? = null

        JarFile(jar).use { jarFile ->
            for (entry in jarFile.entries()) {
                val name = entry.name
                if (entry.isDirectory) {
                    continue
                }

                val contents by lazy {
                    jarFile.getInputStream(entry).use { it.readBytes().toString(Charsets.UTF_8) }
                }

                if (name == "lang.yml") {
                    legacyEnglish = contents
                    continue
                }

                if (!name.startsWith("lang/") || !name.endsWith(".yml")) {
                    continue
                }

                val language = name.removePrefix("lang/").removeSuffix(".yml")
                if (language.isEmpty() || '/' in language) {
                    continue
                }

                files[language.lowercase()] = contents
            }
        }

        legacyEnglish?.let { files.putIfAbsent(LangFiles.ENGLISH, it) }
    }

    companion object {
        /**
         * Reads a plugin's jar, or ships nothing if it can't be read as one, so a plugin loaded
         * some other way still comes up.
         */
        fun open(jar: File, logger: Logger): LangSource = runCatching { JarLangSource(jar) }.getOrElse {
            logger.warning("Could not read language files from ${jar.name}: ${it.message}")
            EmptyLangSource
        }
    }
}

private object EmptyLangSource : LangSource {
    override val languages: Set<String> = emptySet()

    override fun read(language: String): String? = null
}
