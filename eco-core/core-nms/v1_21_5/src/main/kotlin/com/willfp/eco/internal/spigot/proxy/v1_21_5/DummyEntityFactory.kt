package com.willfp.eco.internal.spigot.proxy.v1_21_5

import com.willfp.eco.internal.entities.EcoDummyEntity
import com.willfp.eco.internal.spigot.proxy.DummyEntityFactoryProxy
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.Zombie

class DummyEntityFactory : DummyEntityFactoryProxy {
    override fun createDummyEntity(location: Location): Entity {
        val world = location.world as CraftWorld
        return EcoDummyEntity(world.createEntity(location, Zombie::class.java))
    }
}
