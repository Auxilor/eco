package com.willfp.eco.internal.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.roundToInt

object PriceFactoryXPLevels : PriceFactory {
    override fun getNames() = listOf(
        "l",
        "levels",
        "xplevels",
        "explevels",
    )

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceXPLevel(baseContext) { function.get(it).roundToInt() }
    }

    private class PriceXPLevel(
        private val baseContext: PlaceholderContext,
        private val level: (PlaceholderContext) -> Int
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
            return level(baseContext.copyWithPlayer(player)) * getMultiplier(player) * multiplier
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
