package com.willfp.eco.internal.spigot.proxy.v1_21_7

import com.willfp.eco.internal.spigot.proxy.TPSProxy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

class TPS : TPSProxy {
    override fun getTPS(): Double {
        return (Bukkit.getServer() as CraftServer).handle.server.tps1.average
    }
}
