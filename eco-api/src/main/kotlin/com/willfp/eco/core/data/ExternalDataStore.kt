@file:JvmName("ExternalDataStoreExtensions")

package com.willfp.eco.core.data

/**
 * @see ExternalDataStore.put
 */
fun writeExternalData(
    key: String,
    value: Any
) = ExternalDataStore.put(key, value)

/**
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String
): T? = ExternalDataStore.get(key, T::class.java)

/**
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String,
    default: T
): T = ExternalDataStore.get(key, T::class.java) ?: default

/**
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String,
    default: () -> T
): T = readExternalData(key, default())
