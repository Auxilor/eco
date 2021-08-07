package com.willfp.eco.proxy

import com.willfp.eco.core.proxy.AbstractProxy
import org.bukkit.entity.Player

interface ChatComponentProxy : AbstractProxy {
    fun modifyComponent(
        obj: Any,
        player: Player
    ): Any?
}