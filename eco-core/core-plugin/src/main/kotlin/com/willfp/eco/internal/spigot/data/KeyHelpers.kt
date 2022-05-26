package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.NamespacedKeyUtils

@Suppress("UNCHECKED_CAST")
object KeyHelpers {
    fun deserializeFromString(serialized: String, server: Boolean = false): PersistentDataKey<*>? {
        val split = serialized.split(";").toTypedArray()

        if (split.size < 2) {
            return null
        }

        val key = NamespacedKeyUtils.fromStringOrNull(split[0]) ?: return null
        val type = PersistentDataKeyType.valueOf(split[1]) ?: return null
        val persistentKey = when (type) {
            PersistentDataKeyType.STRING -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<String>,
                if (split.size >= 3) split.toList().subList(2, split.size).joinToString("") else ""
            )
            PersistentDataKeyType.INT -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Int>,
                split[2].toInt()
            )
            PersistentDataKeyType.DOUBLE -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Double>,
                split[2].toDouble()
            )
            PersistentDataKeyType.BOOLEAN -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Boolean>,
                java.lang.Boolean.parseBoolean(split[2])
            )
            else -> null
        }

        if (persistentKey != null) {
            if (server) {
                persistentKey.server()
            } else {
                persistentKey.player()
            }
        }

        return persistentKey
    }

    fun serializeToString(key: PersistentDataKey<*>): String {
        return "${key.key};${key.type.name()};${key.defaultValue}"
    }
}
