@file:JvmName("PersistentDataContainerExtensions")

package com.willfp.eco.core.data

import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

/** @see ExtendedPersistentDataContainer.set */
fun <T : Any, Z : Any> PersistentDataContainer.set(key: String, dataType: PersistentDataType<T, Z>, value: Z) =
    ExtendedPersistentDataContainer.extend(this).set(key, dataType, value)

/** @see ExtendedPersistentDataContainer.has */
fun <T : Any, Z : Any> PersistentDataContainer.has(key: String, dataType: PersistentDataType<T, Z>): Boolean =
    ExtendedPersistentDataContainer.extend(this).has(key, dataType)

/** @see ExtendedPersistentDataContainer.get */
fun <T : Any, Z : Any> PersistentDataContainer.get(key: String, dataType: PersistentDataType<T, Z>): Z? =
    ExtendedPersistentDataContainer.extend(this).get(key, dataType)

/** @see ExtendedPersistentDataContainer.getOrDefault */
fun <T : Any, Z : Any> PersistentDataContainer.getOrDefault(
    key: String,
    dataType: PersistentDataType<T, Z>,
    defaultValue: Z
): Z = ExtendedPersistentDataContainer.extend(this).getOrDefault(key, dataType, defaultValue)

/** @see ExtendedPersistentDataContainer.getAllKeys */
fun PersistentDataContainer.getAllKeys(): Set<String> =
    ExtendedPersistentDataContainer.extend(this).allKeys

/** @see ExtendedPersistentDataContainer.remove */
fun PersistentDataContainer.remove(key: String) =
    ExtendedPersistentDataContainer.extend(this).remove(key)

/** Create a new PDC without the need for an adapter context. */
fun newPersistentDataContainer() =
    ExtendedPersistentDataContainer.create().base
