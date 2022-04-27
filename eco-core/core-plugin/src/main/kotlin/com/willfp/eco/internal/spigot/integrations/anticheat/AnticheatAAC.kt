package com.willfp.eco.internal.spigot.integrations.anticheat

import com.willfp.eco.core.integrations.anticheat.AnticheatIntegration
import me.konsolas.aac.api.AACAPI
import me.konsolas.aac.api.AACExemption
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener

class AnticheatAAC : AnticheatIntegration, Listener {
    private val ecoExemption = AACExemption("eco")
    private val api = Bukkit.getServicesManager().load(AACAPI::class.java)!!

    override fun getPluginName(): String {
        return "AAC"
    }

    override fun exempt(player: Player) {
        api.addExemption(player, ecoExemption)
    }

    override fun unexempt(player: Player) {
        api.removeExemption(player, ecoExemption)
    }
}