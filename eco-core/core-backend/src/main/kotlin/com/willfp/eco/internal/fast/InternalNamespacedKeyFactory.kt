package com.willfp.eco.internal.fast

import org.bukkit.NamespacedKey

class SafeInternalNamespacedKeyFactory {
    fun create(namespace: String, key: String): NamespacedKey {
        return NamespacedKey(namespace, key)
    }
}
