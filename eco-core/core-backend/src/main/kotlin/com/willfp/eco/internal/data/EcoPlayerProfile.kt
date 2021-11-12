package com.willfp.eco.internal.data

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey

class EcoPlayerProfile(
    val data: MutableMap<PersistentDataKey<*>, Any>
) : PlayerProfile {
    override fun <T : Any> write(key: PersistentDataKey<T>, value: T) {
        this.data[key] = value
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
}