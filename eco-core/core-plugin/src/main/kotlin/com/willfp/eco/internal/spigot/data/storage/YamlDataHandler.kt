package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import com.willfp.eco.internal.spigot.data.ProfileHandler
import org.bukkit.NamespacedKey
import java.math.BigDecimal
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    plugin: EcoSpigotPlugin,
    private val handler: ProfileHandler
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
            PersistentDataKeyType.BIG_DECIMAL -> (if (dataYml.has(key.key.toString()))
                BigDecimal(dataYml.getString(key.key.toString())) else null) as T?

            else -> null
        }

        return value
    }

    override fun <T : Any> write(uuid: UUID, key: PersistentDataKey<T>, value: T) {
        doWrite(uuid, key.key, value)
    }

    override fun saveKeysFor(uuid: UUID, keys: Map<PersistentDataKey<*>, Any>) {
        for ((key, value) in keys) {
            doWrite(uuid, key.key, value)
        }
    }

    private fun doWrite(uuid: UUID, key: NamespacedKey, value: Any) {
        dataYml.set("player.$uuid.$key", value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        return other is YamlDataHandler
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}
