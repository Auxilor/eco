package com.willfp.eco.internal.spigot.proxy.v1_21_8

import com.willfp.eco.internal.spigot.proxies.OpenInventoryProxy
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class OpenInventory : OpenInventoryProxy {
    override fun getOpenInventory(player: Player): Any {
        return (player as CraftPlayer).handle.containerMenu
    }
}
