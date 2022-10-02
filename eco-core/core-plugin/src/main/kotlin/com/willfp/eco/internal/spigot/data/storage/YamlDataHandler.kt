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
) : DataHandler(HandlerType.YAML) {
    private val dataYml = plugin.dataYml

    override fun save() {
        dataYml.save()
    }

    override fun <T : Any> read(uuid: UUID, key: PersistentDataKey<T>): T? {
        // Separate `as T?` for each branch to prevent compiler warnings.
        val value = when (key.type) {
            PersistentDataKeyType.INT -> dataYml.getIntOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.DOUBLE -> dataYml.getDoubleOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.STRING -> dataYml.getStringOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.BOOLEAN -> dataYml.getBoolOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.STRING_LIST -> dataYml.getStringsOrNull("player.$uuid.${key.key}") as T?
            PersistentDataKeyType.CONFIG -> dataYml.getSubsectionOrNull("player.$uuid.${key.key}") as T?
            else -> null
        }

        return value
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        doWrite(uuid, key.key, value)
    }

    override fun saveKeysFor(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = handler.loadGenericProfile(uuid)

        for (key in keys) {
            doWrite(uuid, key.key, profile.read(key))
        }
    }

    private fun doWrite(uuid: UUID, key: NamespacedKey, value: Any) {
        dataYml.set("player.$uuid.$key", value)
    }
}
