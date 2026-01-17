package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.willfp.eco.internal.spigot.proxies.TPSProxy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

class TPS : TPSProxy {
    override fun getTPS(): Double {
        return (Bukkit.getServer() as CraftServer).handle.server.msptData5s?.avg ?: 0.0
    }
}
