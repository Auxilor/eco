package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.roundToInt

class PriceFactoryPlayerPoints : PriceFactory {
    override fun getNames() = listOf(
        "player_points",
        "p_points"
    )

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PricePlayerPoints(baseContext) { function.get(it).roundToInt() }
    }

    private class PricePlayerPoints(
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Int
    ) : Price {
        private val api = PlayerPoints.getInstance().api
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return api.look(player.uniqueId) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            api.take(player.uniqueId, getValue(player, multiplier).roundToInt())
        }

        override fun giveTo(player: Player, multiplier: Double) {
            api.give(player.uniqueId, getValue(player, multiplier).roundToInt())
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return function(baseContext.copyWithPlayer(player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier
        }
    }
}
