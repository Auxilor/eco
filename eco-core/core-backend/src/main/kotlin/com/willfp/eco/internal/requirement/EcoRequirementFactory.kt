@file:Suppress("DEPRECATION")

package com.willfp.eco.internal.requirement

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.requirement.Requirement
import com.willfp.eco.core.requirement.RequirementFactory
import org.bukkit.entity.Player

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
class EcoRequirementFactory : RequirementFactory {
    override fun create(name: String): Requirement {
        return when (name.lowercase()) {
            "has-permission" -> RequirementHasPermission()
            "placeholder-equals" -> RequirementPlaceholderEquals()
            "placeholder-greater-than" -> RequirementPlaceholderGreaterThan()
            "placeholder-less-than" -> RequirementPlaceholderLessThan()
            else -> RequirementTrue()
        }
    }
}

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
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

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
class RequirementPlaceholderEquals : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        if (args.size < 2) {
            return false
        }

        val placeholder = args[0]
        val equals = args[1]
        return PlaceholderManager.translatePlaceholders(placeholder, player).equals(equals, ignoreCase = true)
    }
}

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
@SuppressWarnings("DEPRECATION")
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
        return (PlaceholderManager.translatePlaceholders(placeholder, player).toDoubleOrNull() ?: 0.0) >= equals
    }
}

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
class RequirementPlaceholderLessThan : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        if (args.size < 2) {
            return false
        }

        val placeholder = args[0]
        val equals = args[1].toDoubleOrNull() ?: return false
        return (PlaceholderManager.translatePlaceholders(placeholder, player).toDoubleOrNull() ?: 0.0) < equals
    }
}

@Deprecated(
    "Requirement system is marked as for-removal from eco",
    level = DeprecationLevel.WARNING
)
class RequirementTrue : Requirement() {
    override fun doesPlayerMeet(
        player: Player,
        args: List<String>
    ): Boolean {
        return true
    }
}
