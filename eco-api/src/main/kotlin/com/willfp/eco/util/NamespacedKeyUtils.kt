@file:JvmName("NamespacedKeyUtilsExtensions")

package com.willfp.eco.util

/**
 * @see NamespacedKeyUtils.fromString
 */
fun namespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromString(string)

/**
 * @see NamespacedKeyUtils.fromString
 */
fun safeNamespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromStringOrNull(string)

/**
 * @see NamespacedKeyUtils.create
 */
fun namespacedKeyOf(namespace: String, key: String) =
    NamespacedKeyUtils.create(namespace, key)
