package com.willfp.eco.internal.spigot.data.profiles.impl

import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.data.profiles.ProfileHandler
import com.willfp.eco.internal.spigot.data.profiles.isSavedLocally
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class EcoProfile(
    val uuid: UUID,
    private val handler: ProfileHandler
) : Profile {
    private val data = ConcurrentHashMap<PersistentDataKey<*>, Any>()

    override fun <T : Any> write(key: PersistentDataKey<T>, value: T) {
        this.data[key] = value

        handler.profileWriter.write(uuid, key, value)
    }

    override fun <T : Any> read(key: PersistentDataKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (this.data.containsKey(key)) {
            return this.data[key] as T
        }

        this.data[key] = if (key.isSavedLocally) {
            handler.localHandler.read(uuid, key)
        } else {
            handler.defaultHandler.read(uuid, key)
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
}
