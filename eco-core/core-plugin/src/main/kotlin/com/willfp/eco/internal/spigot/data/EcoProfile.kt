package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.data.storage.DataHandler
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class EcoProfile(
    val data: MutableMap<PersistentDataKey<*>, Any>,
    val uuid: UUID,
    private val handler: DataHandler,
    private val localHandler: DataHandler
) : Profile {
    override fun <T : Any> write(key: PersistentDataKey<T>, value: T) {
        this.data[key] = value

        CHANGE_MAP.add(uuid)
    }

    override fun <T : Any> read(key: PersistentDataKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (this.data.containsKey(key)) {
            return this.data[key] as T
        }

        this.data[key] = if (key.isLocal) {
            localHandler.read(uuid, key)
        } else {
            handler.read(uuid, key)
        } ?: key.defaultValue

        return read(key)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoProfile) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return this.uuid.hashCode()
    }

    companion object {
        val CHANGE_MAP: MutableSet<UUID> = ConcurrentHashMap.newKeySet()
    }
}

class EcoPlayerProfile(
    data: MutableMap<PersistentDataKey<*>, Any>,
    uuid: UUID,
    handler: DataHandler,
    localHandler: DataHandler
) : EcoProfile(data, uuid, handler, localHandler), PlayerProfile {
    override fun toString(): String {
        return "EcoPlayerProfile{uuid=$uuid}"
    }
}

class EcoServerProfile(
    data: MutableMap<PersistentDataKey<*>, Any>,
    handler: DataHandler,
    localHandler: DataHandler
) : EcoProfile(data, serverProfileUUID, handler, localHandler), ServerProfile {
    override fun toString(): String {
        return "EcoServerProfile"
    }
}

private val PersistentDataKey<*>.isLocal: Boolean
    get() = EcoPlugin.getPlugin(this.key.namespace)?.isUsingLocalStorage == true
