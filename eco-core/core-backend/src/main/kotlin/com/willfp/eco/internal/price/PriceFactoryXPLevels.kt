package com.willfp.eco.internal.price

import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import java.util.function.Supplier
import kotlin.math.roundToInt

object PriceFactoryXPLevels : PriceFactory {
    override fun getNames() = listOf(
        "l",
        "levels",
        "xplevels",
        "explevels",
    )

    override fun create(function: Supplier<Double>): Price {
        return PriceXPLevel { function.get().roundToInt() }
    }

    private class PriceXPLevel(
        private val levels: () -> Int
    ) : Price {
        override fun canAfford(player: Player) = player.level >= levels()

        override fun pay(player: Player) {
            player.level -= levels()
        }

        override fun giveTo(player: Player) {
            player.level += levels()
        }
    }
}
