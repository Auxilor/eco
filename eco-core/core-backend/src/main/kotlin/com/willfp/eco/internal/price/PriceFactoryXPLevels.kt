package com.willfp.eco.internal.price

import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Function
import kotlin.math.roundToInt

object PriceFactoryXPLevels : PriceFactory {
    override fun getNames() = listOf(
        "l",
        "levels",
        "xplevels",
        "explevels",
    )

    override fun create(baseContext: MathContext, function: Function<MathContext, Double>): Price {
        return PriceXPLevel(baseContext) { function.apply(it).roundToInt() }
    }

    private class PriceXPLevel(
        private val baseContext: MathContext,
        private val level: (MathContext) -> Int
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double) = player.level >= getValue(player, multiplier)

        override fun pay(player: Player, multiplier: Double) {
            player.level -= getValue(player, multiplier).roundToInt()
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.level += getValue(player, multiplier).roundToInt()
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return level(MathContext.copyWithPlayer(baseContext, player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier.roundToInt().toDouble()
        }

        override fun getIdentifier(): String {
            return "eco:xp-levels"
        }
    }
}
