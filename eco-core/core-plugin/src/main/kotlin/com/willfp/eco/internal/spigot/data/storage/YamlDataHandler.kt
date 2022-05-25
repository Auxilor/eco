package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.EcoProfileHandler
import org.bukkit.NamespacedKey
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    plugin: EcoSpigotPlugin,
    private val handler: EcoProfileHandler
) : DataHandler {
    private val dataYml = plugin.dataYml

    override fun save() {
        dataYml.save()
    }

    override fun saveAll(uuids: Iterable<UUID>) {
        for (uuid in uuids) {
            savePlayer(uuid)
        }

        save()
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = handler.loadGenericProfile(uuid)

        for (key in keys) {
            write(uuid, key.key, profile.read(key))
        }
    }

    override fun <T> write(uuid: UUID, key: NamespacedKey, value: T) {
        dataYml.set("player.$uuid.$key", value)
    }

    override fun <T> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        val value = when (key.type) {
            PersistentDataKeyType.INT -> dataYml.getIntOrNull("player.$uuid.${key.key}")
            PersistentDataKeyType.DOUBLE -> dataYml.getDoubleOrNull("player.$uuid.${key.key}")
            PersistentDataKeyType.STRING -> dataYml.getStringOrNull("player.$uuid.${key.key}")
            PersistentDataKeyType.BOOLEAN -> dataYml.getBoolOrNull("player.$uuid.${key.key}")
            else -> null
        } as? T?

        return value
    }
}
