package com.willfp.eco.internal.config.lang

import com.willfp.eco.core.PluginLike
import io.mockk.every
import io.mockk.mockk
import java.nio.file.Path
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * A logger that keeps every warning it is given, so tests can assert on what the owner would see.
 */
class CapturingLogger {
    private val records = mutableListOf<String>()

    val warnings: List<String>
        get() = records

    val logger: Logger = Logger.getAnonymousLogger().apply {
        useParentHandlers = false
        addHandler(object : Handler() {
            override fun publish(record: LogRecord) {
                if (record.level.intValue() >= Level.WARNING.intValue()) {
                    records.add(record.message)
                }
            }

            override fun flush() = Unit

            override fun close() = Unit
        })
    }
}

// Relaxed, so the config handler configs register themselves with is a mock of its own.
fun pluginIn(dir: Path, logger: Logger): PluginLike = mockk<PluginLike>(relaxed = true).also {
    every { it.dataFolder } returns dir.toFile()
    every { it.logger } returns logger
}

/**
 * Shipped language files held in memory, in place of a plugin jar.
 */
class MapLangSource(vararg files: Pair<String, String>) : LangSource {
    private val files = files.toMap()

    override val languages: Set<String>
        get() = files.keys

    override fun read(language: String): String? = files[language]
}
