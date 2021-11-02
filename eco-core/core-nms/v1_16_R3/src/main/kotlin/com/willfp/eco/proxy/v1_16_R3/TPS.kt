package com.willfp.eco.proxy.v1_16_R3

import com.willfp.eco.proxy.TPSProxy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_16_R3.CraftServer

class TPS : TPSProxy {
    override fun getTPS(): Double {
        return (Bukkit.getServer() as CraftServer).handle.server.recentTps[0]
    }
}