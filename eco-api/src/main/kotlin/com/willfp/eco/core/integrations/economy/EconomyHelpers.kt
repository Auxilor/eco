@file:JvmName("EconomyExtensions")

package com.willfp.eco.core.integrations.economy

import org.bukkit.OfflinePlayer
import java.math.BigDecimal

/** @see EconomyManager */
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

/** @see EconomyManager */
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
