package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.core.registry.Registry
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.impl.MariaDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.SQLitePersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler
import java.io.File

abstract class PersistentDataHandlerFactory(
    override val id: String
): KRegistrable {
    abstract fun create(plugin: EcoSpigotPlugin): PersistentDataHandler
}

object PersistentDataHandlers: Registry<PersistentDataHandlerFactory>() {
    init {
        register(object : PersistentDataHandlerFactory("yaml") {
            override fun create(plugin: EcoSpigotPlugin) =
                YamlPersistentDataHandler(plugin)
        })

        register(object : PersistentDataHandlerFactory("sqlite") {
            override fun create(plugin: EcoSpigotPlugin) =
                sqliteHandlerFor(plugin)
        })

        register(object : PersistentDataHandlerFactory("mysql") {
            override fun create(plugin: EcoSpigotPlugin) =
                MySQLPersistentDataHandler(plugin.configYml.getSubsection("mysql"))
        })

        register(object : PersistentDataHandlerFactory("mariadb") {
            override fun create(plugin: EcoSpigotPlugin) =
                MariaDBPersistentDataHandler(plugin.configYml.getSubsection("mysql"))
        })

        register(object : PersistentDataHandlerFactory("mongodb") {
            override fun create(plugin: EcoSpigotPlugin) =
                MongoDBPersistentDataHandler(plugin.configYml.getSubsection("mongodb"))
        })

        // Configs should also accept "mongo"
        register(object : PersistentDataHandlerFactory("mongo") {
            override fun create(plugin: EcoSpigotPlugin) =
                MongoDBPersistentDataHandler(plugin.configYml.getSubsection("mongodb"))
        })
    }
}

/**
 * Resolve a configured `data-handler` value to the id of the handler that serves it.
 *
 * yaml is no longer a data handler, but it is the value every long-lived server has in its config,
 * so it resolves to sqlite rather than failing the boot.
 */
fun resolveHandlerId(configured: String): String {
    val id = configured.lowercase()
    return if (id == "yaml") "sqlite" else id
}

/** The SQLite handler for a plugin's own data folder, as both the default and the local handler. */
fun sqliteHandlerFor(plugin: EcoSpigotPlugin) = SQLitePersistentDataHandler(
    File(plugin.dataFolder, plugin.configYml.getStringOrNull("sqlite.file") ?: "data.db"),
    plugin.configYml.getStringOrNull("sqlite.prefix") ?: "eco_"
)

/**
 * The handlers to shut down, with duplicates removed.
 *
 * On a sqlite server the local and default handlers are one object, and shutdown drains that
 * handler's executor -- so shutting it down twice drains an already drained executor.
 */
fun handlersToShutdown(vararg handlers: PersistentDataHandler): List<PersistentDataHandler> =
    handlers.distinct()
