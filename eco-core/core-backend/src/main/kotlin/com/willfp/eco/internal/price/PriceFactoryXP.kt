package com.willfp.eco.internal.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.roundToInt

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

        override fun canAfford(player: Player, multiplier: Double): Boolean =
            player.totalExperience >= getValue(player, multiplier)

        override fun pay(player: Player, multiplier: Double) {
            player.totalExperience -= getValue(player, multiplier).roundToInt()
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.totalExperience += getValue(player, multiplier).roundToInt()
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
