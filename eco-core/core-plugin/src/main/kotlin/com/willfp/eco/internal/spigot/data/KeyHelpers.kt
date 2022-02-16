package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.NamespacedKeyUtils

@Suppress("UNCHECKED_CAST")
object KeyHelpers {
    fun deserializeFromString(serialized: String): PersistentDataKey<*>? {
        val split = serialized.split("::").toTypedArray()
        if (split.size != 3) {
            return null
        }
        val key = NamespacedKeyUtils.fromStringOrNull(split[0]) ?: return null
        val type = PersistentDataKeyType.valueOf(split[1]) ?: return null
        return when (type.name()) {
            "STRING" -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<String>,
                split[2]
            )
            "INT" -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Int>, split[2].toInt()
            )
            "DOUBLE" -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Double>, split[2].toDouble()
            )
            "BOOLEAN" -> PersistentDataKey(
                key,
                type as PersistentDataKeyType<Boolean>,
                java.lang.Boolean.parseBoolean(split[2])
            )
            else -> null
        }
    }

    fun serializeToString(key: PersistentDataKey<*>): String {
        return "${key.key}::${key.type.name()}::${key.defaultValue}"
    }
}