package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.data.ExtendedPersistentDataContainer
import org.bukkit.persistence.PersistentDataContainer

interface ExtendedPersistentDataContainerFactoryProxy {
    fun adapt(pdc: PersistentDataContainer): ExtendedPersistentDataContainer
    fun newPdc(): PersistentDataContainer
}
