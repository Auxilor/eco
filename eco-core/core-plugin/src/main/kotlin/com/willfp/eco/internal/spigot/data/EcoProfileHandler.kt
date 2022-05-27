package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ProfileHandler
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.storage.DataHandler
import com.willfp.eco.internal.spigot.data.storage.HandlerType
import com.willfp.eco.internal.spigot.data.storage.MongoDataHandler
import com.willfp.eco.internal.spigot.data.storage.MySQLDataHandler
import com.willfp.eco.internal.spigot.data.storage.YamlDataHandler
import org.bukkit.Bukkit
import java.util.UUID

val serverProfileUUID = UUID(0, 0)

class EcoProfileHandler(
    private val type: HandlerType,
    private val plugin: EcoSpigotPlugin
) : ProfileHandler {
    private val loaded = mutableMapOf<UUID, Profile>()

    val handler: DataHandler = when (type) {
        HandlerType.YAML -> YamlDataHandler(plugin, this)
        HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
        HandlerType.MONGO -> MongoDataHandler(plugin, this)
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

    override fun load(uuid: UUID): PlayerProfile {
        return loadGenericProfile(uuid) as PlayerProfile
    }

    override fun loadServerProfile(): ServerProfile {
        return loadGenericProfile(serverProfileUUID) as ServerProfile
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        handler.saveKeysFor(uuid, keys)
    }

    override fun unloadPlayer(uuid: UUID) {
        loaded.remove(uuid)
    }

    override fun save() {
        handler.save()
    }

    private fun migrateIfNeeded() {
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

        plugin.logger.info("eco has detected a change in data handler!")
        plugin.logger.info("Migrating server data from ${previousHandlerType.name} to ${type.name}")
        plugin.logger.info("This will take a while!")

        val players = Bukkit.getOfflinePlayers().map { it.uniqueId }

        plugin.logger.info("Found data for ${players.size} players!")

        /*
        Declared here as its own function to be able to use T.
         */
        fun <T : Any> migrateKey(uuid: UUID, key: PersistentDataKey<T>, from: DataHandler, to: DataHandler) {
            val category = Eco.getHandler().keyRegistry.getCategory(key)
            if (category != null) {
                from.categorize(key, category)
            }
            val previous: T? = from.read(uuid, key)
            if (previous != null) {
                to.write(uuid, key, previous)
            }
        }

        var i = 1
        for (uuid in players) {
            plugin.logger.info("Migrating data for $uuid... ($i / ${players.size})")

            for (key in PersistentDataKey.values()) {
                migrateKey(uuid, key, previousHandler, handler)
            }

            i++
        }

        plugin.logger.info("Updating previous handler...")
        plugin.dataYml.set("previous-handler", type.name)
        plugin.dataYml.save()
        plugin.logger.info("Done!")
    }

    fun initialize() {
        plugin.dataYml.getStrings("categorized-keys.player")
            .mapNotNull { KeyHelpers.deserializeFromString(it) }

        plugin.dataYml.getStrings("categorized-keys.server")
            .mapNotNull { KeyHelpers.deserializeFromString(it, server = true) }

        handler.initialize()

        migrateIfNeeded()
    }
}
