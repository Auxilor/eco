package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.lang.reflect.Method
import java.util.Optional

class AntigriefCombatLogX : AntigriefIntegration {
    private val plugin = Bukkit.getPluginManager().getPlugin("CombatLogX")
    private var disabled = false

    private inner class Handles {
        val manager: Any? = plugin?.javaClass?.methods?.first { it.name == "getExpansionManager" }?.invoke(plugin)
        val getExpansion: Method? = manager?.javaClass?.methods?.firstOrNull {
            it.name == "getExpansion" && it.parameterCount == 1 && it.parameterTypes[0] == String::class.java
        }
        var pvpCheck: ((expansion: Any, player: Player, victim: Player) -> Boolean)? = null
    }

    private val handles: Handles? by lazy {
        runCatching { Handles() }.getOrNull()
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        if (victim !is Player) return true
        if (disabled) return true
        val h = handles ?: return true
        if (h.getExpansion == null) return true
        if (h.manager == null) return true

        return runCatching {
            val optional = h.getExpansion.invoke(h.manager, "NewbieHelper") as Optional<*>
            if (!optional.isPresent) return true
            val expansion = optional.get()
            if (h.pvpCheck == null) {
                h.pvpCheck = buildPvpCheck(expansion)
            }
            h.pvpCheck?.invoke(expansion, player, victim) ?: true
        }.onFailure { disabled = true }.getOrDefault(true)
    }

    private fun buildPvpCheck(expansion: Any): ((Any, Player, Player) -> Boolean)? {
        val getProtectionManager = expansion.javaClass.methods.find { it.name == "getProtectionManager" } ?: return null
        val getPvpManager = expansion.javaClass.methods.find { it.name == "getPvpManager" } ?: return null
        val samplePm = getProtectionManager.invoke(expansion) ?: return null
        val samplePvm = getPvpManager.invoke(expansion) ?: return null
        val isProtected = samplePm.javaClass.methods.find { it.name == "isProtected" && it.parameterCount == 1 } ?: return null
        val isDisabled = samplePvm.javaClass.methods.find { it.name == "isDisabled" && it.parameterCount == 1 } ?: return null

        return { exp: Any, p: Player, v: Player ->
            val pm = getProtectionManager.invoke(exp)
            val pvm = getPvpManager.invoke(exp)
            val isUnprotectedVictim = !(isProtected.invoke(pm, v) as Boolean)
            val isEnabledVictim = !(isDisabled.invoke(pvm, v) as Boolean)
            val isEnabledPlayer = !(isDisabled.invoke(pvm, p) as Boolean)
            isUnprotectedVictim && isEnabledVictim && isEnabledPlayer
        }
    }

    override fun canBreakBlock(player: Player, block: Block) = true
    override fun canCreateExplosion(player: Player, location: Location) = true
    override fun canPlaceBlock(player: Player, block: Block) = true
    override fun canPickupItem(player: Player, location: Location) = true

    override fun getPluginName() = "CombatLogX"

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefIntegration) return false
        return other.pluginName == this.pluginName
    }

    override fun hashCode() = this.pluginName.hashCode()
}