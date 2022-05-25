package com.willfp.eco.internal.spigot.data

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
import java.util.UUID

val serverProfileUUID = UUID(0, 0)

class EcoProfileHandler(
    type: HandlerType,
    plugin: EcoSpigotPlugin
) : ProfileHandler {
    private val loaded = mutableMapOf<UUID, Profile>()

    val handler: DataHandler = when(type) {
        HandlerType.YAML -> YamlDataHandler(plugin, this)
        HandlerType.MYSQL -> MySQLDataHandler(plugin, this)
        HandlerType.MONGO -> MongoDataHandler(plugin)
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
        val profile = loadGenericProfile(uuid)

        for (key in keys) {
            handler.write(uuid, key.key, profile.read(key))
        }
    }

    override fun unloadPlayer(uuid: UUID) {
        loaded.remove(uuid)
    }

    override fun saveAll() {
        handler.saveAll(loaded.keys.toList())
    }

    override fun save() {
        handler.save()
    }

    fun initialize() {
        handler.initialize()
    }
}