package com.willfp.eco.spigot.integrations.afk

import com.Zrips.CMI.CMI
import com.willfp.eco.core.integrations.afk.AFKWrapper
import org.bukkit.entity.Player

class AFKIntegrationCMI : AFKWrapper {
    override fun isAfk(player: Player): Boolean {
        return CMI.getInstance().playerManager.getUser(player)?.isAfk ?: false
    }

    override fun getPluginName(): String {
        return "CMI"
    }
}
