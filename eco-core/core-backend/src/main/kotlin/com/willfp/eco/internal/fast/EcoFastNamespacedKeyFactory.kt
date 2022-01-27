package com.willfp.eco.internal.fast

import org.bukkit.NamespacedKey
import org.objenesis.ObjenesisSerializer

object EcoFastNamespacedKeyFactory {
    private val creator = ObjenesisSerializer().getInstantiatorOf(NamespacedKey::class.java)
    private val namespaceField = NamespacedKey::class.java.getDeclaredField("namespace")
        .apply { isAccessible = true }
    private val keyField = NamespacedKey::class.java.getDeclaredField("key")
        .apply { isAccessible = true }


    fun create(namespace: String, key: String): NamespacedKey {
        val namespacedKey = creator.newInstance()
        namespaceField.set(keyField, key.lowercase())
        namespaceField.set(namespaceField, namespace.lowercase())
        return namespacedKey
    }
}