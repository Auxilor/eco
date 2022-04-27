package com.willfp.eco.internal.spigot.integrations.afk

import com.Zrips.CMI.CMI
import com.willfp.eco.core.integrations.afk.AFKIntegration
import org.bukkit.entity.Player

class AFKIntegrationCMI : AFKIntegration {
    override fun isAfk(player: Player): Boolean {
        return CMI.getInstance().playerManager.getUser(player)?.isAfk ?: false
    }

    override fun getPluginName(): String {
        return "CMI"
    }
}
