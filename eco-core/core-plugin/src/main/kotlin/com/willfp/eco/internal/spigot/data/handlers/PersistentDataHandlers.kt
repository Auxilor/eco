package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.core.registry.Registry
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.handlers.impl.MongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler

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

        register(object : PersistentDataHandlerFactory("mysql") {
            override fun create(plugin: EcoSpigotPlugin) =
                MySQLPersistentDataHandler(plugin.configYml.getSubsection("mysql"))
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
