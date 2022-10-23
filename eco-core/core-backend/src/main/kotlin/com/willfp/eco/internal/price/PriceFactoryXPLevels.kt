package com.willfp.eco.internal.price

import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.bukkit.entity.Player
import kotlin.math.roundToInt

object PriceFactoryXPLevels : PriceFactory {
    override fun getNames() = listOf(
        "levels",
        "xp levels",
        "exp levels",
        "l",
        "xpl",
        "expl"
    )

    override fun create(value: Double): Price = PriceXPLevel(value.roundToInt())

    private class PriceXPLevel(
        private val levels: Int
    ) : Price {
        override fun canAfford(player: Player) = player.level >= levels

        override fun pay(player: Player) {
            player.level -= levels
        }
    }
}
