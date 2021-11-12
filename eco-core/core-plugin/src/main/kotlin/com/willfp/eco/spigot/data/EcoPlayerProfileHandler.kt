package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.PlayerProfileHandler
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.data.EcoPlayerProfile
import com.willfp.eco.spigot.EcoSpigotPlugin
import org.bukkit.Bukkit
import java.util.*

class EcoPlayerProfileHandler(
    private val plugin: EcoSpigotPlugin
) : PlayerProfileHandler {
    private val loaded = mutableMapOf<UUID, PlayerProfile>()
    private val handler = plugin.dataHandler

    override fun load(uuid: UUID): PlayerProfile {
        val found = loaded[uuid]
        if (found != null) {
            return found
        }

        val data = mutableMapOf<PersistentDataKey<*>, Any>()

        for (key in Eco.getHandler().keyRegistry.registeredKeys) {
            data[key] = handler.read(uuid, key.key) ?: key.defaultValue
        }

        val profile = EcoPlayerProfile(data)
        loaded[uuid] = profile
        return profile
    }

    override fun unloadPlayer(uuid: UUID) {
        loaded.remove(uuid)
    }

    override fun savePlayer(uuid: UUID) {
        handler.savePlayer(uuid)
    }

    override fun savePlayerBlocking(uuid: UUID) {
        handler.saveAllBlocking(listOf(uuid))
    }

    override fun saveAll() {
        handler.saveAll(loaded.keys.toList())
    }

    override fun saveAllBlocking() {
        handler.saveAllBlocking(loaded.keys.toList())
    }

    fun autosave() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return
        }

        if (plugin.configYml.getBool("autosave.log")) {
            plugin.logger.info("Auto-Saving player data!")
        }

        saveAll()

        if (plugin.configYml.getBool("autosave.log")) {
            plugin.logger.info("Saved player data!")
        }
    }
}