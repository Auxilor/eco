package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.entities.ai.EntityController
import org.bukkit.entity.Mob

interface EntityControllerFactoryProxy {
    fun <T: Mob> createEntityController(
        entity: T
    ): EntityController<T>
}
