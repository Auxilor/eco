@file:JvmName("NamespacedKeyUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.EcoPlugin

/**
 * Create a NamespacedKey by splitting a string around its first colon, where the part before
 * the colon is the namespace and everything after it is the key.
 *
 * @param string The string, in `namespace:key` form.
 * @return The key.
 * @throws NullPointerException If the string contains no colon.
 * @see NamespacedKeyUtils.fromString
 */
fun namespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromString(string)

/**
 * Create a NamespacedKey by splitting a string around its first colon, returning null instead
 * of throwing if the string is not a valid key.
 *
 * @param string The string, in `namespace:key` form.
 * @return The key, or null if the string contains no colon.
 * @see NamespacedKeyUtils.fromStringOrNull
 */
fun safeNamespacedKeyOf(string: String) =
    NamespacedKeyUtils.fromStringOrNull(string)

/**
 * Create a NamespacedKey from an explicit namespace and key.
 *
 * @param namespace The namespace.
 * @param key       The key.
 * @return The key.
 * @see NamespacedKeyUtils.create
 */
fun namespacedKeyOf(namespace: String, key: String) =
    NamespacedKeyUtils.create(namespace, key)

/**
 * Create a NamespacedKey in a plugin's own namespace.
 *
 * @param plugin The plugin whose namespace to use.
 * @param key    The key.
 * @return The key.
 * @see EcoPlugin.createNamespacedKey
 */
fun namespacedKeyOf(plugin: EcoPlugin, key: String) =
    plugin.createNamespacedKey(key)
