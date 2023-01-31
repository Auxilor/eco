package com.willfp.eco.internal.command

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.PluginCommandBase
import org.bukkit.Bukkit

class EcoPluginCommand(
    parentDelegate: CommandBase,
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean
) : HandledCommand(parentDelegate, plugin, name, permission, playersOnly),
    PluginCommandBase {
    override fun register() {
        val command = Bukkit.getPluginCommand(name)

        if (command == null) {
            unregister()

            commandMap.register(plugin.name.lowercase(), DelegatedBukkitCommand(this))
        } else {
            command.setExecutor(this)


            description?.let {
                command.setDescription(it)
            }

            if (aliases.isNotEmpty()) {
                command.aliases = aliases
            }
        }

        Eco.get().syncCommands()
    }

    override fun unregister() {
        val found = commandMap.getCommand(name)
        found?.unregister(commandMap)

        Eco.get().syncCommands()
    }
}

class EcoSubcommand(
    parentDelegate: CommandBase,
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean
) : HandledCommand(parentDelegate, plugin, name, permission, playersOnly)
