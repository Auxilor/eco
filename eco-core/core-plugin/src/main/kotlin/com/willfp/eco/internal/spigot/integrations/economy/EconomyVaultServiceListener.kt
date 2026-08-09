package com.willfp.eco.internal.spigot.integrations.economy

import com.willfp.eco.core.integrations.economy.EconomyManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServiceRegisterEvent
import org.bukkit.event.server.ServiceUnregisterEvent
import org.bukkit.plugin.ServicesManager

/**
 * Keeps the Vault economy integration in sync with Bukkit's [ServicesManager].
 *
 * The initial lookup for an [Economy] provider happens during [onEnable][org.bukkit.plugin.Plugin],
 * but the actual provider (e.g. CMI, EssentialsX) may not have registered itself with Vault yet at
 * that point, as plugin enable order between two plugins that both merely soft-depend on Vault is
 * not guaranteed. This listener re-resolves the registration whenever it changes, so a late (or
 * later-replaced) provider is still picked up without requiring a restart.
 */
class EconomyVaultServiceListener(
    private val servicesManager: ServicesManager
) : Listener {
    @EventHandler
    fun onServiceRegister(event: ServiceRegisterEvent) {
        if (event.provider.service != Economy::class.java) {
            return
        }

        refresh()
    }

    @EventHandler
    fun onServiceUnregister(event: ServiceUnregisterEvent) {
        if (event.provider.service != Economy::class.java) {
            return
        }

        refresh()
    }

    private fun refresh() {
        val rsp = servicesManager.getRegistration(Economy::class.java)
        if (rsp != null) {
            EconomyManager.register(EconomyVault(rsp.provider))
        } else {
            EconomyManager.unregister("Vault")
        }
    }
}
