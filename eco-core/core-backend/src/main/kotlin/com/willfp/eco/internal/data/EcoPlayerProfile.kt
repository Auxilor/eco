package com.willfp.eco.internal.data

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class EcoPlayerProfile(
    val data: MutableMap<PersistentDataKey<*>, Any>,
    val uuid: UUID
) : PlayerProfile {
    override fun <T : Any> write(key: PersistentDataKey<T>, value: T) {
        this.data[key] = value

        val changedKeys = CHANGE_MAP[uuid] ?: mutableSetOf()
        changedKeys.add(key)
        CHANGE_MAP[uuid] = changedKeys
    }

    override fun <T : Any> read(key: PersistentDataKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        return this.data[key] as T? ?: key.defaultValue
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoPlayerProfile) {
            return false
        }

        return this.data == other.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "EcoPlayerProfile{$data}"
    }

    companion object {
        val CHANGE_MAP: MutableMap<UUID, MutableSet<PersistentDataKey<*>>> = ConcurrentHashMap()
    }
}