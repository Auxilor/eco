package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import me.TechsCode.UltraEconomy.UltraEconomy
import me.TechsCode.UltraEconomy.objects.Account
import me.TechsCode.UltraEconomy.objects.Currency
import org.bukkit.entity.Player
import java.util.UUID
import java.util.function.Function

class PriceFactoryUltraEconomy(private val currency: Currency) : PriceFactory {
    override fun getNames(): List<String> {
        return currency.name.lowercase().toSingletonList()
    }

    override fun create(baseContext: MathContext, function: Function<MathContext, Double>): Price {
        return PriceUltraEconomy(currency, baseContext) { function.apply(it) }
    }

    private class PriceUltraEconomy(
        private val currency: Currency,
        private val baseContext: MathContext,
        private val function: (MathContext) -> Double
    ) : Price {
        private val multipliers = mutableMapOf<UUID, Double>()
        private val api = UltraEconomy.getAPI()

        private val Player.account: Account?
            get() = api.accounts.uuid(this.uniqueId).orElse(null)

        override fun canAfford(player: Player, multiplier: Double): Boolean {
            return (player.account?.getBalance(currency)?.onHand ?: 0f) >= getValue(player, multiplier)
        }

        override fun pay(player: Player, multiplier: Double) {
            player.account?.getBalance(currency)?.removeHand(getValue(player, multiplier).toFloat())
        }

        override fun giveTo(player: Player, multiplier: Double) {
            player.account?.getBalance(currency)?.addHand(getValue(player, multiplier).toFloat())
        }

        override fun getValue(player: Player, multiplier: Double): Double {
            return function(MathContext.copyWithPlayer(baseContext, player)) * getMultiplier(player) * multiplier
        }

        override fun getMultiplier(player: Player): Double {
            return multipliers[player.uniqueId] ?: 1.0
        }

        override fun setMultiplier(player: Player, multiplier: Double) {
            multipliers[player.uniqueId] = multiplier
        }
    }
}
