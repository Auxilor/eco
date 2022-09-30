package com.willfp.eco.internal.spigot.integrations.economy

import com.willfp.eco.core.integrations.economy.EconomyIntegration
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer
import java.math.BigDecimal

class EconomyVault(
    private val vault: Economy
) : EconomyIntegration {
    override fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault.has(player, amount.toDouble())
    }

    override fun giveMoney(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault.depositPlayer(player, amount.toDouble()).transactionSuccess()
    }

    override fun removeMoney(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault.withdrawPlayer(player, amount.toDouble()).transactionSuccess()
    }

    override fun getExactBalance(player: OfflinePlayer): BigDecimal {
        return vault.getBalance(player).toBigDecimal()
    }

    override fun getPluginName(): String {
        return "Vault"
    }
}
