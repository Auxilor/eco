@file:JvmName("EconomyExtensions")

package com.willfp.eco.core.integrations.economy

import java.math.BigDecimal
import org.bukkit.OfflinePlayer

/**
 * The player's balance.
 *
 * Setting this gives or removes the difference between the current and the new balance;
 * setting a value of zero or below empties the balance instead.
 *
 * @see EconomyManager.getBalance
 * @see EconomyManager.giveMoney
 * @see EconomyManager.removeMoney
 */
var OfflinePlayer.balance: Double
    get() = EconomyManager.getBalance(this)
    set(value) {
        if (value <= 0) {
            EconomyManager.removeMoney(this, this.balance)
            return
        }

        val diff = this.balance - value

        if (diff > 0) {
            EconomyManager.removeMoney(this, diff)
        } else if (diff < 0) {
            EconomyManager.giveMoney(this, -diff)
        }
    }

/**
 * The player's balance as a [BigDecimal], for use with [BigDecimal] arithmetic.
 *
 * The getter converts the [Double] balance, so it carries no more precision than [balance];
 * use [EconomyManager.getExactBalance] directly if full precision is required. Setting this
 * behaves as [balance] does, but gives and removes exact [BigDecimal] amounts.
 *
 * @see EconomyManager.getBalance
 * @see EconomyManager.giveMoney
 * @see EconomyManager.removeMoney
 */
var OfflinePlayer.exactBalance: BigDecimal
    get() = EconomyManager.getBalance(this).toBigDecimal()
    set(value) {
        if (value <= BigDecimal.ZERO) {
            EconomyManager.removeMoney(this, this.exactBalance)
            return
        }

        val diff = this.exactBalance - value

        if (diff > BigDecimal.ZERO) {
            EconomyManager.removeMoney(this, diff)
        } else if (diff < BigDecimal.ZERO) {
            EconomyManager.giveMoney(this, -diff)
        }
    }
