package com.willfp.eco.internal.spigot.proxy

import org.bukkit.entity.Player

interface ChatComponentProxy {
    fun modifyComponent(
        obj: Any,
        player: Player
    ): Any
}