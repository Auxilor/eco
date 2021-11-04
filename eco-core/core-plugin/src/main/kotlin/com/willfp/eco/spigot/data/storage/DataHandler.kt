package com.willfp.eco.spigot.data.storage

import org.bukkit.NamespacedKey
import java.util.*

interface DataHandler {
    fun save() {

    }

    fun updateKeys() {

    }

    fun <T> write(uuid: UUID, key: NamespacedKey, value: T)
    fun savePlayer(uuid: UUID)
    fun <T> read(uuid: UUID, key: NamespacedKey): T?
}