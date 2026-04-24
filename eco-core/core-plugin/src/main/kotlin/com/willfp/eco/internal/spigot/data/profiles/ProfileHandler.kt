package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.ServerLocking
import com.willfp.eco.internal.spigot.data.KeyRegistry
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlerFactory
import com.willfp.eco.internal.spigot.data.handlers.PersistentDataHandlers
import com.willfp.eco.internal.spigot.data.handlers.impl.LegacyMongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.LegacyMySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MongoDBPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.MySQLPersistentDataHandler
import com.willfp.eco.internal.spigot.data.handlers.impl.YamlPersistentDataHandler
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoPlayerProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.EcoServerProfile
import com.willfp.eco.internal.spigot.data.profiles.impl.serverProfileUUID
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val LEGACY_MIGRATED_KEY = "legacy-data-migrated"

class ProfileHandler(
    private val plugin: EcoSpigotPlugin
) {
    private val handlerId = plugin.configYml.getString("data-handler")

    val localHandler = YamlPersistentDataHandler(plugin)
    val defaultHandler = PersistentDataHandlers[handlerId]?.create(plugin)
        ?: throw IllegalArgumentException("Invalid data handler ($handlerId)")

    val profileWriter = ProfileWriter(plugin, this)

    private val loaded = ConcurrentHashMap<UUID, EcoProfile>()

    fun getPlayerProfile(uuid: UUID): EcoPlayerProfile {
        return loaded.computeIfAbsent(uuid) {
            EcoPlayerProfile(it, this)
        } as EcoPlayerProfile
    }

    fun getServerProfile(): EcoServerProfile {
        return loaded.computeIfAbsent(serverProfileUUID) {
            EcoServerProfile(this)
        } as EcoServerProfile
    }

    fun unloadProfile(uuid: UUID) {
        loaded.remove(uuid)
    }

    fun save() {
        localHandler.shutdown()
        defaultHandler.shutdown()
    }

    fun migrateIfNecessary(): Boolean {
        if (!plugin.configYml.getBool("perform-data-migration")) {
            return false
        }

        // First install
        if (!plugin.dataYml.has("previous-handler")) {
            plugin.dataYml.set("previous-handler", defaultHandler.id)
            plugin.dataYml.set(LEGACY_MIGRATED_KEY, true)
            plugin.dataYml.save()
            return false
        }

        val previousHandlerId = plugin.dataYml.getString("previous-handler").lowercase()
        if (previousHandlerId != defaultHandler.id) {
            val fromFactory = PersistentDataHandlers[previousHandlerId] ?: return false
            scheduleMigration(fromFactory)

            return true
        }

        if (defaultHandler is MySQLPersistentDataHandler && !plugin.dataYml.getBool(LEGACY_MIGRATED_KEY)) {
            plugin.logger.info("eco has detected a legacy MySQL database. Migrating to new MySQL database...")
            scheduleMigration(LegacyMySQLPersistentDataHandler.Factory)

            return true
        }

        if (defaultHandler is MongoDBPersistentDataHandler && !plugin.dataYml.getBool(LEGACY_MIGRATED_KEY)) {
            plugin.logger.info("eco has detected a legacy MongoDB database. Migrating to new MongoDB database...")
            scheduleMigration(LegacyMongoDBPersistentDataHandler.Factory)

            return true
        }

        return false
    }

    private fun scheduleMigration(fromFactory: PersistentDataHandlerFactory) {
        ServerLocking.lock("Migrating player data! Check console for more information.")

        // Run after 5 ticks to allow plugins to load their data keys
        plugin.scheduler.runTaskLater(5) {
            doMigrate(fromFactory)

            plugin.dataYml.set(LEGACY_MIGRATED_KEY, true)
            plugin.dataYml.save()
        }
    }

    private fun doMigrate(fromFactory: PersistentDataHandlerFactory) {
        plugin.logger.info("eco has detected a change in data handler")
        plugin.logger.info("${fromFactory.id} --> ${defaultHandler.id}")
        plugin.logger.info("This will take a while! Players will not be able to join during this time.")

        val fromHandler = fromFactory.create(plugin)
        val toHandler = defaultHandler

        val keys = KeyRegistry.getRegisteredKeys()

        plugin.logger.info("Keys to migrate: ${keys.map { it.key }.joinToString(", ") }}")

        plugin.logger.info("Loading profile UUIDs from ${fromFactory.id}...")
        plugin.logger.info("This step may take a while depending on the size of your database.")

        val uuids = fromHandler.getSavedUUIDs()

        plugin.logger.info("Found ${uuids.size} profiles to migrate")

        for ((index, uuid) in uuids.withIndex()) {
            plugin.logger.info("(${index + 1}/${uuids.size}) Migrating $uuid")
            val profile = fromHandler.serializeProfile(uuid, keys)
            toHandler.loadSerializedProfile(profile)
        }

        plugin.logger.info("Profile writes submitted! Waiting for completion...")
        toHandler.shutdown()

        plugin.logger.info("Updating previous handler...")
        plugin.dataYml.set("previous-handler", handlerId)
        plugin.dataYml.save()
        plugin.logger.info("The server will now automatically be restarted...")

        plugin.server.shutdown()
    }
}
