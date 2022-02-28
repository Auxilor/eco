package com.willfp.eco.internal.spigot.proxy

import org.bukkit.Location
import org.bukkit.entity.Entity

interface DummyEntityFactoryProxy {
    fun createDummyEntity(
        location: Location
    ): Entity
}