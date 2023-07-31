package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import me.TechsCode.UltraEconomy.UltraEconomy
import me.TechsCode.UltraEconomy.objects.Account
import me.TechsCode.UltraEconomy.objects.Currency
import org.bukkit.entity.Player
import java.util.UUID

class PriceFactoryUltraEconomy(private val currency: Currency) : PriceFactory {
    override fun getNames(): List<String> {
        return currency.name.lowercase().toSingletonList()
    }

    override fun create(baseContext: PlaceholderContext, function: PlaceholderContextSupplier<Double>): Price {
        return PriceUltraEconomy(currency, baseContext) { function.get(it) }
    }

    private class PriceUltraEconomy(
        private val currency: Currency,
        private val baseContext: PlaceholderContext,
        private val function: (PlaceholderContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()
        private val api = UltraEconomy.getAPI()

        private val Player.account: Account?
            get() = api.accounts.uuid(this.uniqueId).orElse(null)

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return (player.account?.getBalance(currency)?.onHand ?: 0.0) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            player.account?.getBalance(currency)?.removeHand(getValue(player, multiplier))
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.account?.getBalance(currency)?.addHand(getValue(player, multiplier))
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
