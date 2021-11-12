package com.willfp.eco.spigot.data

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.PlayerProfileHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.data.EcoPlayerProfile
import com.willfp.eco.spigot.data.storage.DataHandler
import java.util.*

class EcoPlayerProfileHandler(
    private val handler: DataHandler
) : PlayerProfileHandler {
    private val loaded = mutableMapOf<UUID, PlayerProfile>()

    override fun load(uuid: UUID): PlayerProfile {
        val found = loaded[uuid]
        if (found != null) {
            return found
        }

        val data = mutableMapOf<PersistentDataKey<*>, Any>()

        for (key in PersistentDataKey.values()) {
            data[key] = handler.read(uuid, key.key) ?: key.defaultValue
        }

        val profile = EcoPlayerProfile(data, uuid)
        loaded[uuid] = profile
        return profile
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
}