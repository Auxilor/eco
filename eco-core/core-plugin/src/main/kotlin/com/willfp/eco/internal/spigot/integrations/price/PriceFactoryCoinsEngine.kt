package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import org.bukkit.entity.Player
import su.nightexpress.coinsengine.api.CoinsEngineAPI
import su.nightexpress.coinsengine.api.currency.Currency
import java.util.*

class PriceFactoryCoinsEngine(private val currency: Currency) : PriceFactory {

    override fun getNames(): List<String?> {
        return currency.id.lowercase().toSingletonList()
    }

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double?>): Price {
        return PriceCoinsEngine(currency, baseContext) { function.get(it) }
    }

    private class PriceCoinsEngine(
        private val currency: Currency,
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return CoinsEngineAPI.getBalance(player.uniqueId, currency) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            CoinsEngineAPI.removeBalance(player.uniqueId, currency, getValue(player, multiplier))
        }

        override fun giveTo(player: Player, multiplier: Double) {
            CoinsEngineAPI.addBalance(player.uniqueId, currency, getValue(player, multiplier))
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