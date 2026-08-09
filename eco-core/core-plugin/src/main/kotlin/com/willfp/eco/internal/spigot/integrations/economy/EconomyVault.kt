package com.willfp.eco.internal.spigot.integrations.economy

import com.willfp.eco.core.integrations.economy.EconomyIntegration
import java.math.BigDecimal
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class EconomyVault : EconomyIntegration {
    private val vault: Economy?
        get() = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider

    override fun hasAmount(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault?.has(player, amount.toDouble()) ?: false
    }

    override fun giveMoney(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault?.depositPlayer(player, amount.toDouble())?.transactionSuccess() ?: false
    }

    override fun removeMoney(player: OfflinePlayer, amount: BigDecimal): Boolean {
        return vault?.withdrawPlayer(player, amount.toDouble())?.transactionSuccess() ?: false
    }

    override fun getExactBalance(player: OfflinePlayer): BigDecimal {
        return vault?.getBalance(player)?.toBigDecimal() ?: BigDecimal.ZERO
    }

    override fun getPluginName(): String {
        return "Vault"
    }
}
