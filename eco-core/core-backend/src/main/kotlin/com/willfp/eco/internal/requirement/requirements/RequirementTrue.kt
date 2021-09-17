package com.willfp.eco.internal.requirement.requirements

import com.willfp.eco.core.requirement.Requirement
import org.bukkit.entity.Player

class RequirementTrue : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        return true
    }
}
