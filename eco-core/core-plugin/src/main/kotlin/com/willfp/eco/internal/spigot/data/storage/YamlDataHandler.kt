package com.willfp.eco.internal.spigot.data.storage

import com.willfp.eco.core.config.yaml.YamlBaseConfig
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.EcoSpigotPlugin
import org.bukkit.NamespacedKey
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    plugin: EcoSpigotPlugin
) : DataHandler {
    private val dataYml = DataYml(plugin)

    override fun save() {
        dataYml.save()
    }

    override fun saveAll(uuids: Iterable<UUID>) {
        for (uuid in uuids) {
            savePlayer(uuid)
        }

        save()
    }

    override fun saveKeysForPlayer(uuid: UUID, keys: Set<PersistentDataKey<*>>) {
        val profile = PlayerProfile.load(uuid)

        for (key in keys) {
            write(uuid, key.key, profile.read(key))
        }
    }

    override fun <T> write(uuid: UUID, key: NamespacedKey, value: T) {
        dataYml.set("player.$uuid.$key", value)
    }

    override fun <T> read(uuid: UUID, key: NamespacedKey): T? {
        return dataYml.get("player.$uuid.$key") as T?
    }

    class DataYml(
        plugin: EcoSpigotPlugin
    ) : YamlBaseConfig(
        "data",
        false,
        plugin
    )
}