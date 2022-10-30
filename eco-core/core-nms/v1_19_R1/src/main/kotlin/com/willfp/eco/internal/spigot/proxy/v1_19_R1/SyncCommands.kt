package com.willfp.eco.internal.spigot.proxy.v1_19_R1

import com.willfp.eco.internal.spigot.proxy.SyncCommandsProxy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.CraftServer

class SyncCommands : SyncCommandsProxy {
    override fun syncCommands() {
        (Bukkit.getServer() as CraftServer).syncCommands()
    }
}
