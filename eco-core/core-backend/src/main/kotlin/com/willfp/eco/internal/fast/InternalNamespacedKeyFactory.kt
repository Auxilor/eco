package com.willfp.eco.internal.fast

import org.bukkit.NamespacedKey
import org.objenesis.ObjenesisStd

interface InternalNamespacedKeyFactory {
    fun create(namespace: String, key: String): NamespacedKey
}

class FastInternalNamespacedKeyFactory : InternalNamespacedKeyFactory {
    private val creator = ObjenesisStd().getInstantiatorOf(NamespacedKey::class.java)
    private val namespaceField = NamespacedKey::class.java.getDeclaredField("namespace")
        .apply { isAccessible = true }
    private val keyField = NamespacedKey::class.java.getDeclaredField("key")
        .apply { isAccessible = true }


    override fun create(namespace: String, key: String): NamespacedKey {
        val namespacedKey = creator.newInstance()
        keyField.set(namespacedKey, key.lowercase())
        namespaceField.set(namespacedKey, namespace.lowercase())
        return namespacedKey
    }
}

class SafeInternalNamespacedKeyFactory : InternalNamespacedKeyFactory {
    override fun create(namespace: String, key: String): NamespacedKey {
        return NamespacedKey(namespace, key)
    }
}
