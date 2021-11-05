package com.willfp.eco.spigot.data.storage

import org.bukkit.NamespacedKey
import java.util.UUID

interface DataHandler {
    fun save() {

    }

    fun saveAll(uuids: Iterable<UUID>)

    fun saveAllBlocking(uuids: Iterable<UUID>) {
        saveAll(uuids)
    }

    fun updateKeys() {

    }

    fun <T> write(uuid: UUID, key: NamespacedKey, value: T)
    fun savePlayer(uuid: UUID)
    fun <T> read(uuid: UUID, key: NamespacedKey): T?
}