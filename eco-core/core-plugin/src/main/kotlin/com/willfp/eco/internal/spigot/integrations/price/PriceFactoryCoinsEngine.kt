package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import java.util.UUID
import org.bukkit.entity.Player
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency

class PriceFactoryCoinsEngine(
    private val api: ExcellentEconomyAPI,
    private val currency: ExcellentCurrency
) : PriceFactory {

    override fun getNames(): List<String?> {
        return currency.id.lowercase().toSingletonList()
    }

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double?>): Price {
        return PriceCoinsEngine(api, currency, baseContext) { function.get(it) }
    }

    private class PriceCoinsEngine(
        private val api: ExcellentEconomyAPI,
        private val currency: ExcellentCurrency,
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return api.getBalance(player, currency) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            api.withdraw(player, currency, getValue(player, multiplier))
        }

        override fun giveTo(player: Player, multiplier: Double) {
            api.deposit(player, currency, getValue(player, multiplier))
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