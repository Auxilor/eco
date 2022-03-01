package com.willfp.eco.internal.spigot.proxy.v1_17_R1

import com.willfp.eco.core.entities.ai.ControlledEntity
import com.willfp.eco.internal.spigot.proxy.ControlledEntityFactoryProxy
import com.willfp.eco.internal.spigot.proxy.v1_17_R1.ai.EcoControlledEntity
import org.bukkit.entity.Mob

class ControlledEntityFactory : ControlledEntityFactoryProxy {
    override fun createControlledEntity(entity: Mob): ControlledEntity {
        return EcoControlledEntity(entity)
    }
}
