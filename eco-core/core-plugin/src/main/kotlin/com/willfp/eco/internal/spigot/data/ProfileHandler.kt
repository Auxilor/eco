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
import com.willfp.eco.internal.spigot.data.storage.MongoDataHandler
import com.willfp.eco.internal.spigot.data.storage.MySQLDataHandler
import com.willfp.eco.internal.spigot.data.storage.YamlDataHandler
import org.bukkit.Bukkit
import java.util.UUID

val serverProfileUUID = UUID(0, 0)

class ProfileHandler(
    private val type: HandlerType,
    private val plugin: EcoSpigotPlugin
) {
    private val loaded = mutableMapOf<UUID, EcoProfile>()

    private val localHandler = YamlDataHandler(plugin, this)

    val handler: DataHandler = when (type) {
        HandlerType.YAML -> localHandler
        HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
        HandlerType.MONGO -> MongoDataHandler(plugin, this)
    }

    fun accessLoadedProfile(uuid: UUID): EcoProfile? =
        loaded[uuid]

    fun loadGenericProfile(uuid: UUID): Profile {
        val found = loaded[uuid]
        if (found != null) {
            return found
        }

        val data = mutableMapOf<PersistentDataKey<*>, Any>()

        val profile = if (uuid == serverProfileUUID)
            EcoServerProfile(data, handler, localHandler) else EcoPlayerProfile(data, uuid, handler, localHandler)

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
        val profile = accessLoadedProfile(uuid) ?: return
        val map = mutableMapOf<PersistentDataKey<*>, Any>()

        for (key in keys) {
            map[key] = profile.data[key] ?: continue
        }

        handler.saveKeysFor(uuid, map)

        // Don't save to local handler if it's the same handler.
        if (localHandler != handler) {
            localHandler.saveKeysFor(uuid, map)
        }
    }

    fun unloadPlayer(uuid: UUID) {
        loaded.remove(uuid)
    }

    fun save() {
        handler.save()

        if (localHandler != handler) {
            localHandler.save()
        }
    }

    fun migrateIfNeeded() {
        if (!plugin.configYml.getBool("perform-data-migration")) {
            return
        }

        if (!plugin.dataYml.has("previous-handler")) {
            plugin.dataYml.set("previous-handler", type.name)
            plugin.dataYml.save()
        }


        val previousHandlerType = HandlerType.valueOf(plugin.dataYml.getString("previous-handler"))

        if (previousHandlerType == type) {
            return
        }

        val previousHandler = when (previousHandlerType) {
            HandlerType.YAML -> YamlDataHandler(plugin, this)
            HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
            HandlerType.MONGO -> MongoDataHandler(plugin, this)
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
        if (localHandler != handler) {
            localHandler.initialize()
        }
    }
}
