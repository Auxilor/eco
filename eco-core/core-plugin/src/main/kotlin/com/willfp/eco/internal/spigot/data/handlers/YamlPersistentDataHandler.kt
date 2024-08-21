package com.willfp.eco.internal.spigot.data.handlers

import com.willfp.eco.core.data.handlers.PersistentDataHandler
import com.willfp.eco.core.data.handlers.SerializedProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import java.util.UUID

class YamlPersistentDataHandler(
    plugin: EcoSpigotPlugin
) : PersistentDataHandler("yaml") {
    private val dataYml = plugin.dataYml

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        // Separate `as T?` for each branch to prevent compiler warnings.
        val value = when (key.type) {
            PersistentDataKeyType.INT -> dataYml.getIntOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.DOUBLE -> dataYml.getDoubleOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.STRING -> dataYml.getStringOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.BOOLEAN -> dataYml.getBoolOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.STRING_LIST -> dataYml.getStringsOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.CONFIG -> dataYml.getSubsectionOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.BIG_DECIMAL -> dataYml.getBigDecimalOrNull("player.$uuid.${key.key}") as T?

            else -> null
        }

        return value
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        dataYml.set("player.$uuid.$key", value)
    }

    override fun serializeData(keys: Set<PersistentDataKey<*>>): Set<SerializedProfile> {
        val profiles = mutableSetOf<SerializedProfile>()
        val uuids = dataYml.getSubsection("player").getKeys(false).map { UUID.fromString(it) }

        for (uuid in uuids) {
            val data = mutableMapOf<PersistentDataKey<*>, Any>()

            for (key in keys) {
                data[key] = read(uuid, key) ?: continue
            }

            profiles.add(SerializedProfile(uuid, data))
        }

        return profiles
    }

    override fun loadProfileData(data: Set<SerializedProfile>) {
        for (profile in data) {
            for ((key, value) in profile.data) {
                // Dirty cast, but it's fine because we know it's the same type
                @Suppress("UNCHECKED_CAST")
                write(profile.uuid, key as PersistentDataKey<Any>, value as Any)
            }
        }
    }
}
