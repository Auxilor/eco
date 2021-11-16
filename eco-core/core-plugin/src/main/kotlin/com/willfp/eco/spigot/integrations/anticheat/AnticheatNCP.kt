package com.willfp.eco.spigot.integrations.anticheat

import com.willfp.eco.core.integrations.anticheat.AnticheatWrapper
import fr.neatmonster.nocheatplus.checks.CheckType
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager
import org.bukkit.entity.Player
import java.util.UUID

class AnticheatNCP : AnticheatWrapper {
    private val exempt: MutableSet<UUID> = HashSet()
    override fun getPluginName(): String {
        return "NCP"
    }

    override fun exempt(player: Player) {
        if (NCPExemptionManager.isExempted(player, CheckType.ALL)) {
            return
        }
        if (exempt.add(player.uniqueId)) {
            NCPExemptionManager.exemptPermanently(player, CheckType.ALL)
        }
    }

    override fun unexempt(player: Player) {
        if (exempt.remove(player.uniqueId)) {
            NCPExemptionManager.unexempt(player, CheckType.ALL)
        }
    }
}