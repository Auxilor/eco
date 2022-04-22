@file:JvmName("ExtendedPersistentDataContainerExtensions")

package com.willfp.eco.core.data

import org.bukkit.persistence.PersistentDataContainer

/**
 * @see ExtendedPersistentDataContainer.wrap
 */
val PersistentDataContainer.extended: ExtendedPersistentDataContainer
    get() = ExtendedPersistentDataContainer.wrap(this)
