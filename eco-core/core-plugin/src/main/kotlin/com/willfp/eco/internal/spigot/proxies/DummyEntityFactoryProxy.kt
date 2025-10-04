package com.willfp.eco.internal.spigot.proxies

import org.bukkit.Location
import org.bukkit.entity.Entity

interface DummyEntityFactoryProxy {
    fun createDummyEntity(
        location: Location
    ): Entity
}