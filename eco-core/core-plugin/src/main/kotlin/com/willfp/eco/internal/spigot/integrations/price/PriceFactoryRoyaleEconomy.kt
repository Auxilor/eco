package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import me.qKing12.RoyaleEconomy.MultiCurrency.Currency
import org.bukkit.entity.Player
import java.util.*

class PriceFactoryRoyaleEconomy(private val currency: Currency) : PriceFactory {

    override fun getNames(): List<String> {
        return currency.currencyId.lowercase().toSingletonList()
    }

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceRoyaleEconomy(currency, baseContext) { function.get(it) }
    }

    private class PriceRoyaleEconomy(
        private val currency: Currency,
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return currency.getAmount(player.uniqueId.toString()) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            currency.removeAmount(player.uniqueId.toString(), getValue(player, multiplier))
        }

        override fun giveTo(player: Player, multiplier: Double) {
            currency.addAmount(player.uniqueId.toString(), getValue(player, multiplier))
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