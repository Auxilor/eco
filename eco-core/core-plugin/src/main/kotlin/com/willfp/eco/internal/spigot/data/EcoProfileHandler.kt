package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.profile
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.ServerLocking
import com.willfp.eco.internal.spigot.data.storage.DataHandler
import com.willfp.eco.internal.spigot.data.storage.HandlerType
import com.willfp.eco.internal.spigot.data.storage.LegacyMySQLDataHandler
import com.willfp.eco.internal.spigot.data.storage.MongoDataHandler
import com.willfp.eco.internal.spigot.data.storage.MySQLDataHandler
import com.willfp.eco.internal.spigot.data.storage.YamlDataHandler
import org.bukkit.Bukkit
import java.util.UUID

val serverProfileUUID = UUID(0, 0)

class EcoProfileHandler(
    private val type: HandlerType,
    private val plugin: EcoSpigotPlugin
) {
    private val loaded = mutableMapOf<UUID, Profile>()

    val handler: DataHandler = when (type) {
        HandlerType.YAML -> YamlDataHandler(plugin, this)
        HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
        HandlerType.MONGO -> MongoDataHandler(plugin, this)
        HandlerType.LEGACY_MYSQL -> LegacyMySQLDataHandler(plugin, this)
    }

    init {
        if (handler.type == HandlerType.LEGACY_MYSQL) {
            plugin.logger.warning("You're using the legacy MySQL handler!")
            plugin.logger.warning("Some features will not work and you may get unfixable errors.")
            plugin.logger.warning("Support cannot be given to data issues related to legacy MySQL.")
            plugin.logger.warning("Change your data handler to mysql, mongo, or yaml to fix this!")
            plugin.logger.warning("This can be done in /plugins/eco/config.yml")
        }
    }

    fun loadGenericProfile(uuid: UUID): Profile {
        val found = loaded[uuid]
        if (found != null) {
            return found
        }

        val data = mutableMapOf<PersistentDataKey<*>, Any>()

        val profile = if (uuid == serverProfileUUID)
            EcoServerProfile(data, handler) else EcoPlayerProfile(data, uuid, handler)

        loaded[uuid] = profile
        return profile
    }

    fun load(uuid: UUID): PlayerProfile {
        return loadGenericProfile(uuid) as PlayerProfile
    }

    fun loadServerProfile(): ServerProfile {
        return loadGenericProfile(serverProfileUUID) as ServerProfile
    }

    fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        handler.saveKeysFor(uuid, keys)
    }

    fun unloadPlayer(uuid: UUID) {
        loaded.remove(uuid)
    }

    fun save() {
        handler.save()
    }

    fun migrateIfNeeded() {
        if (!plugin.configYml.getBool("perform-data-migration")) {
            return
        }

        if (!plugin.dataYml.has("previous-handler")) {
            plugin.dataYml.set("previous-handler", type.name)
            plugin.dataYml.save()
        }


        var previousHandlerType = HandlerType.valueOf(plugin.dataYml.getString("previous-handler"))

        if (previousHandlerType == HandlerType.MYSQL && !plugin.dataYml.has("new-mysql")) {
            previousHandlerType = HandlerType.LEGACY_MYSQL
        }

        if (previousHandlerType == type) {
            return
        }

        val previousHandler = when (previousHandlerType) {
            HandlerType.YAML -> YamlDataHandler(plugin, this)
            HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
            HandlerType.MONGO -> MongoDataHandler(plugin, this)
            HandlerType.LEGACY_MYSQL -> LegacyMySQLDataHandler(plugin, this)
        }

        ServerLocking.lock("Migrating player data! Check console for more information.")

        plugin.logger.info("eco has detected a change in data handler!")
        plugin.logger.info("Migrating server data from ${previousHandlerType.name} to ${type.name}")
        plugin.logger.info("This will take a while!")

        plugin.logger.info("Initializing previous handler...")
        previousHandler.initialize()

        val players = Bukkit.getOfflinePlayers().map { it.uniqueId }

        plugin.logger.info("Found data for ${players.size} players!")

        /*
        Declared here as its own function to be able to use T.
         */
        fun <T : Any> migrateKey(uuid: UUID, key: PersistentDataKey<T>, from: DataHandler, to: DataHandler) {
            val previous: T? = from.read(uuid, key)
            if (previous != null) {
                Bukkit.getOfflinePlayer(uuid).profile.write(key, previous) // Nope, no idea.
                to.write(uuid, key, previous)
            }
        }

        var i = 1
        for (uuid in players) {
            plugin.logger.info("Migrating data for $uuid... ($i / ${players.size})")
            for (key in PersistentDataKey.values()) {
                // Why this? Because known points *really* likes to break things with the legacy MySQL handler.
                if (key.key.key == "known_points") {
                    continue
                }

                try {
                    migrateKey(uuid, key, previousHandler, handler)
                } catch (e: Exception) {
                    plugin.logger.info("Could not migrate ${key.key} for $uuid! This is probably because they do not have any data.")
                }
            }

            i++
        }

        plugin.logger.info("Saving new data...")
        handler.save()
        plugin.logger.info("Updating previous handler...")
        plugin.dataYml.set("previous-handler", type.name)
        plugin.dataYml.save()
        plugin.logger.info("The server will now automatically be restarted...")

        ServerLocking.unlock()

        Bukkit.getServer().shutdown()
    }

    fun initialize() {
        handler.initialize()
    }
}
