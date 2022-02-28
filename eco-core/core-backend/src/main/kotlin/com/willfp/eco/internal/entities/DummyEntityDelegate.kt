package com.willfp.eco.internal.entities

import org.bukkit.entity.Entity

class DummyEntityDelegate(private val handle: Entity) : Entity by handle {
    override fun toString(): String {
        return "DummyEntity{id=${this.entityId}}"
    }
}
