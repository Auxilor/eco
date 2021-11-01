package com.willfp.eco.spigot.data.storage

import com.willfp.eco.core.config.yaml.YamlBaseConfig
import com.willfp.eco.spigot.EcoSpigotPlugin
import org.bukkit.NamespacedKey
import java.util.*

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    plugin: EcoSpigotPlugin
) : DataHandler {
    private val dataYml = DataYml(plugin)

    override fun save() {
        dataYml.save()
    }

    override fun updateKeys() {
        // Do nothing
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