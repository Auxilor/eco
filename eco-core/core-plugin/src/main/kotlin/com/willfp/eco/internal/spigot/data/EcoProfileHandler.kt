package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ProfileHandler
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.data.storage.DataHandler
import java.util.UUID

val serverProfileUUID = UUID(0, 0)

class EcoProfileHandler(
    private val handler: DataHandler
) : ProfileHandler {
    private val loaded = mutableMapOf<UUID, Profile>()

    private fun loadGenericProfile(uuid: UUID): Profile {
        val found = loaded[uuid]
        if (found != null) {
            return found
        }

        val data = mutableMapOf<PersistentDataKey<*>, Any>()

        return if (uuid == serverProfileUUID) {
            val profile = EcoServerProfile(data, handler)
            loaded[uuid] = profile
            profile
        } else {
            val profile = EcoPlayerProfile(data, uuid, handler)
            loaded[uuid] = profile
            profile
        }
    }

    override fun load(uuid: UUID): PlayerProfile {
        return loadGenericProfile(uuid) as PlayerProfile
    }


    override fun loadServerProfile(): ServerProfile {
        return loadGenericProfile(serverProfileUUID) as ServerProfile
    }

    override fun saveKeysForPlayer(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = PlayerProfile.load(uuid)

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

    fun updateKeys() {
        handler.updateKeys()
    }
}