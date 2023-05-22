package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.ServerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.data.storage.DataHandler
import com.willfp.eco.util.namespacedKeyOf
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

private val serverIDKey = PersistentDataKey(
    namespacedKeyOf("eco", "server_id"),
    PersistentDataKeyType.STRING,
    ""
)

private val localServerIDKey = PersistentDataKey(
    namespacedKeyOf("eco", "local_server_id"),
    PersistentDataKeyType.STRING,
    ""
)

class EcoServerProfile(
    data: MutableMap<PersistentDataKey<*>, Any>,
    handler: DataHandler,
    localHandler: DataHandler
) : EcoProfile(data, serverProfileUUID, handler, localHandler), ServerProfile {
    override fun getServerID(): String {
        if (this.read(serverIDKey).isBlank()) {
            this.write(serverIDKey, UUID.randomUUID().toString())
        }

        return this.read(serverIDKey)
    }

    override fun getLocalServerID(): String {
        if (this.read(localServerIDKey).isBlank()) {
            this.write(localServerIDKey, UUID.randomUUID().toString())
        }

        return this.read(localServerIDKey)
    }

    override fun toString(): String {
        return "EcoServerProfile"
    }
}

private val PersistentDataKey<*>.isLocal: Boolean
    get() = this == localServerIDKey || EcoPlugin.getPlugin(this.key.namespace)?.isUsingLocalStorage == true
