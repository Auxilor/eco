package com.willfp.eco.internal.command

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.PluginCommandBase
import org.bukkit.Bukkit

/**
 * EcoPluginCommand contains the internal logic of PluginCommand.
 */
class EcoPluginCommand(
    plugin: EcoPlugin, name: String,
    permission: String,
    playersOnly: Boolean
) : EcoHandledCommand(plugin, name, permission, playersOnly),
    PluginCommandBase {
    override fun register() {
        val command = Bukkit.getPluginCommand(name)
        command?.let { c ->
            c.setExecutor(this)

            description?.let {
                command.setDescription(it)
            }

            aliases.firstOrNull()?.let {
                command.setAliases(aliases)
            }
        } ?: run {
            unregister()

            val map = getCommandMap()

            map.register(plugin.name.lowercase(), EcoDelegatedBukkitCommand(this))
        }

        Eco.get().syncCommands()
    }

    override fun unregister() {
        val map = getCommandMap()
        val found = map.getCommand(name)
        found?.unregister(map)

        Eco.get().syncCommands()
    }
}