@file:JvmName("NamespacedKeyUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.EcoPlugin

/** @see NamespacedKeyUtils.fromString */
fun namespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromString(string)

/** @see NamespacedKeyUtils.fromString */
fun safeNamespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromStringOrNull(string)

/** @see NamespacedKeyUtils.create */
fun namespacedKeyOf(namespace: String, key: String) =
    NamespacedKeyUtils.create(namespace, key)

/** @see EcoPlugin.namespacedKeyFactory */
fun namespacedKeyOf(plugin: EcoPlugin, key: String) =
    plugin.createNamespacedKey(key)
