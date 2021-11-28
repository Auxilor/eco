package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.ChatComponentProxy
import org.bukkit.entity.Player

class ChatComponent : ChatComponentProxy {
    override fun modifyComponent(obj: Any, player: Player): Any {
        return obj
    }
}
