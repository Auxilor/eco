package com.willfp.eco.internal.drops

import com.willfp.eco.core.drops.DropQueueFactory
import com.willfp.eco.core.drops.InternalDropQueue
import com.willfp.eco.internal.drops.impl.EcoDropQueue
import com.willfp.eco.internal.drops.impl.EcoFastCollatedDropQueue
import org.bukkit.entity.Player

object EcoDropQueueFactory : DropQueueFactory {
    override fun create(player: Player): InternalDropQueue {
        return if (DropManager.type == DropQueueType.COLLATED) EcoFastCollatedDropQueue(player) else EcoDropQueue(
            player
        )
    }
}