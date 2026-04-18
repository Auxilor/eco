package com.willfp.eco.internal.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.roundToInt

private fun getXPNeededForLevel(level: Int): Int {
    // XP Formula from NMS Player
    return if (level >= 30) 112 + (level - 30) * 9 else (if (level >= 15) 37 + (level - 15) * 5 else 7 + level * 2)
}

object PriceFactoryXP : PriceFactory {
    override fun getNames() = listOf(
        "xp",
        "exp",
        "experience"
    )

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceXP(baseContext) { function.get(it).roundToInt() }
    }

    private class PriceXP(
        private val baseContext: PlaceholderContext,
        private val xp: (PlaceholderContext) -> Int
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            var totalExperience = 0
            for (level in 0 until player.level) {
                totalExperience += getXPNeededForLevel(level)
            }

            totalExperience += (player.exp * getXPNeededForLevel(player.level)).toInt()

            return totalExperience >= getValue(player, multiplier).roundToInt()
        }

        override fun pay(player: Player, multiplier: Double) {
            takeXP(player, getValue(player, multiplier).roundToInt())
        }

        private fun takeXP(player: Player, amount: Int) {
            val currentLevel = player.level
            val currentExp = player.exp * getXPNeededForLevel(currentLevel)

            if (currentExp >= amount) {
                player.exp = (currentExp - amount) / getXPNeededForLevel(currentLevel)
            } else {
                // Handle recursive level down
                player.exp = 1f
                player.level = (currentLevel - 1).coerceAtLeast(0)
                takeXP(player, (amount - currentExp).toInt())
            }
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.giveExp(getValue(player, multiplier).roundToInt())
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return xp(baseContext.copyWithPlayer(player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier.roundToInt().toDouble()
        }

        override fun getIdentifier(): String {
            return "eco:xp"
        }
    }
}
