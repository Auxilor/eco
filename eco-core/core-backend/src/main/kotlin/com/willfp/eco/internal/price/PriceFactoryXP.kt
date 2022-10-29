package com.willfp.eco.internal.price

import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.function.Supplier
import kotlin.math.roundToInt

object PriceFactoryXP : PriceFactory {
    override fun getNames() = listOf(
        "xp",
        "exp",
        "experience"
    )

    override fun create(function: Supplier<Double>): Price {
        return PriceXP { function.get().roundToInt() }
    }

    private class PriceXP(
        private val xp: () -> Int
    ) : Price {
        private var multiplier = 1.0

        override fun canAfford(player: Player) = player.totalExperience >= value

        override fun pay(player: Player) {
            player.totalExperience -= value.roundToInt()
        }

        override fun giveTo(player: Player) {
            player.totalExperience += value.roundToInt()
        }

        override fun getValue(): Double {
            return xp() * multiplier
        }

        override fun getMultiplier() = multiplier

        override fun setMultiplier(multiplier: Double) {
            this.multiplier = multiplier
        }
    }
}
