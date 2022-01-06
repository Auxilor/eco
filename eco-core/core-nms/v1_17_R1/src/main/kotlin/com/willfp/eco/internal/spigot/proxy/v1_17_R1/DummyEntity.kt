package com.willfp.eco.internal.spigot.proxy.v1_17_R1

import com.willfp.eco.internal.spigot.proxy.DummyEntityProxy
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

class DummyEntity : DummyEntityProxy {
    override fun createDummyEntity(location: Location): Entity {
        val world = location.world as CraftWorld
        return world.createEntity(location, EntityType.ZOMBIE.entityClass).bukkitEntity
    }
}