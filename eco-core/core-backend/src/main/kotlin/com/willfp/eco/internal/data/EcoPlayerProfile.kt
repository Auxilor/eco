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
}