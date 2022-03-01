package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.entities.ai.ControlledEntity
import org.bukkit.entity.Mob

interface ControlledEntityFactoryProxy {
    fun createControlledEntity(
        entity: Mob
    ): ControlledEntity
}
