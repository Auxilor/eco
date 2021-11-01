package com.willfp.eco.spigot.data.storage

import org.bukkit.NamespacedKey
import java.util.*

interface DataHandler {
    fun save()

    fun <T> write(uuid: UUID, key: NamespacedKey, value: T)
    fun <T> read(uuid: UUID, key: NamespacedKey): T?

    fun updateKeys()
}