@file:JvmName("ExternalDataStoreExtensions")

package com.willfp.eco.core.data

/**
 * Write data to the store.
 *
 * @param key The key.
 * @param value The value.
 * @see ExternalDataStore.put
 */
fun writeExternalData(
    key: String,
    value: Any
) = ExternalDataStore.put(key, value)

/**
 * Read data of the reified type from the store.
 *
 * @param key The key.
 * @return The value, or null if there is nothing stored under the key or the stored value is
 * not of the reified type.
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String
): T? = ExternalDataStore.get(key, T::class.java)

/**
 * Read data of the reified type from the store, falling back to a default value.
 *
 * @param key The key.
 * @param default The default value.
 * @return The value, or the default if there is nothing stored under the key or the stored
 * value is not of the reified type.
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String,
    default: T
): T = ExternalDataStore.get(key, T::class.java) ?: default

/**
 * Read data of the reified type from the store, falling back to a supplied default value.
 *
 * The supplier is always invoked, even when a stored value is present.
 *
 * @param key The key.
 * @param default The supplier of the default value.
 * @return The value, or the supplied default if there is nothing stored under the key or the
 * stored value is not of the reified type.
 * @see ExternalDataStore.get
 */
inline fun <reified T> readExternalData(
    key: String,
    default: () -> T
): T = readExternalData(key, default())
