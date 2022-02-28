package com.willfp.eco.internal.entities

import com.willfp.eco.core.entities.DummyEntity
import org.bukkit.entity.Entity

class EcoDummyEntity(private val handle: Entity) : DummyEntity, Entity by handle {
    override fun toString(): String {
        return "DummyEntity{id=${this.entityId}}"
    }
}
