package com.willfp.eco.internal.requirement.requirements

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.requirement.Requirement
import org.bukkit.entity.Player

class RequirementPlaceholderGreaterThan : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        if (args.size < 2) {
            return false
        }

        val placeholder = args[0]
        val equals = args[1].toDoubleOrNull() ?: return false
        return PlaceholderManager.translatePlaceholders(placeholder, player).toDoubleOrNull() ?: 0.0 >= equals
    }
}