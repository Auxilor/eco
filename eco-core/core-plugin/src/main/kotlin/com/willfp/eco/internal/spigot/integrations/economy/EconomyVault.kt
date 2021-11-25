package com.willfp.eco.internal.spigot.integrations.economy

import com.willfp.eco.core.integrations.economy.EconomyWrapper
import net.milkbowl.vault.economy.Economy
import org.bukkit.OfflinePlayer

class EconomyVault(
    private val vault: Economy
): EconomyWrapper {
    override fun hasAmount(player: OfflinePlayer, amount: Double): Boolean {
        return vault.has(player, amount)
    }

    override fun giveMoney(player: OfflinePlayer, amount: Double): Boolean {
        return vault.depositPlayer(player, amount).transactionSuccess()
    }

    override fun removeMoney(player: OfflinePlayer, amount: Double): Boolean {
        return vault.withdrawPlayer(player, amount).transactionSuccess()
    }

    override fun getBalance(player: OfflinePlayer): Double {
        return vault.getBalance(player)
    }

    override fun getPluginName(): String {
        return "Vault"
    }
}