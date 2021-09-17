package com.willfp.eco.internal.requirement.requirements

import com.willfp.eco.core.requirement.Requirement
import org.bukkit.entity.Player

class RequirementHasPermission : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        if (args.isEmpty()) {
            return false
        }

        val permission = args[0]
        return player.hasPermission(permission)
    }
}