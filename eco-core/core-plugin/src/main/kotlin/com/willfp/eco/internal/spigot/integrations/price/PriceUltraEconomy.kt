package com.willfp.eco.internal.spigot.integrations.price

import com.willfp.eco.core.math.MathContext
import com.willfp.eco.core.price.Price
import com.willfp.eco.core.price.PriceFactory
import com.willfp.eco.util.toSingletonList
import me.TechsCode.UltraEconomy.UltraEconomy
import me.TechsCode.UltraEconomy.objects.Currency
import org.bukkit.entity.Player
import java.util.function.Function

class PriceUltraEconomy(private val currency: Currency): PriceFactory {

    /**
     * Get the names (how the price looks in lookup strings).
     *
     *
     * For example, for XP Levels this would be 'l', 'xpl', 'levels', etc.
     *
     * @return The allowed names.
     */
    override fun getNames(): MutableList<String> {
        return "ultraeconomy:${currency.name.lowercase()}".toSingletonList().toMutableList()
    }

    /**
     * Create the price.
     *
     * @param baseContext The base MathContext.
     * @param function    The function to use. Should use {@link MathContext#copyWithPlayer(MathContext, Player)}
     * on calls.
     * @return The price.
     */
    override fun create(baseContext: MathContext, function: Function<MathContext, Double>): Price {
        return UEPriceObject(currency, function, baseContext)
    }

    class UEPriceObject(
        private val currency: Currency, private val function: Function<MathContext, Double>,
        private val baseContext: MathContext): Price {
        private val api = UltraEconomy.getAPI()

        /**
         * Get if the player can afford the price.
         *
         * @param player The player.
         * @return If the player can afford.
         */
        override fun canAfford(player: Player): Boolean {
            return (api.accounts.firstOrNull { it.uuid == player.uniqueId }
                ?.getBalance(currency)?.onHand?.toDouble()?: 0.0) >= (function.apply(
                MathContext.copyWithPlayer(baseContext, player)))
        }

        /**
         * Make the player pay the price.
         *
         *
         * Only run this if the player can afford the price.
         *
         * @param player The player.
         */
        override fun pay(player: Player) {
            api.accounts.firstOrNull { it.uuid == player.uniqueId }
                ?.getBalance(currency)
                ?.removeHand(function.apply(MathContext.copyWithPlayer(baseContext, player)).toFloat())
        }

    }
}