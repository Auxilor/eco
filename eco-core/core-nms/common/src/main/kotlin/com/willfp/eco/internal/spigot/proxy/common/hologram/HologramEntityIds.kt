package com.willfp.eco.internal.spigot.proxy.common.hologram

import java.util.concurrent.atomic.AtomicInteger
import net.minecraft.world.entity.Entity

private val ENTITY_COUNTER: AtomicInteger = run {
    val field = Entity::class.java.getDeclaredField("ENTITY_COUNTER")
    field.isAccessible = true
    field.get(null) as AtomicInteger
}

/** Allocate a fresh entity id from Minecraft's shared counter. */
fun nextHologramEntityId(): Int = ENTITY_COUNTER.incrementAndGet()
