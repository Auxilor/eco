@file:JvmName("EconomyExtensions")

package com.willfp.eco.core.integrations.economy

import org.bukkit.OfflinePlayer

/**
 * @see EconomyManager
 */
var OfflinePlayer.balance: Double
    get() = EconomyManager.getBalance(this)
    set(value) {
        val diff = this.balance - value

        if (diff > 0) {
            EconomyManager.removeMoney(this, diff)
        } else if (diff < 0) {
            EconomyManager.giveMoney(this, -diff)
        }
    }
