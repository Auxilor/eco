package com.willfp.eco.internal.spigot.proxy.v26_1_1

import com.willfp.eco.internal.spigot.proxies.TPSProxy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

class TPS : TPSProxy {
    override fun getTPS(): Double {
        return (Bukkit.getServer() as CraftServer).tps[0]
    }
}
