package com.willfp.eco.internal.command

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.PluginCommandBase
import org.bukkit.Bukkit

class EcoPluginCommand(
    private val parentDelegate: PluginCommandBase,
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean
) : HandledCommand(parentDelegate, plugin, name, permission, playersOnly),
    PluginCommandBase {
    override fun register() {
        val command = Bukkit.getPluginCommand(name)

        val knownDescription = command?.description ?: description ?: ""
        val knownAliases = command?.aliases ?: aliases

        if (command == null) {
            unregister()
            commandMap.register(plugin.name.lowercase(), DelegatedBukkitCommand(this))
        } else {
            command.setExecutor(this)
            command.tabCompleter = this
            command.description = knownDescription
            command.aliases = knownAliases
        }

        Eco.get().syncCommands()
    }

    override fun unregister() {
        commandMap.getCommand(name)?.unregister(commandMap)
        Eco.get().unregisterCommand(this)

        Eco.get().syncCommands()
    }

    override fun getAliases(): List<String> = parentDelegate.aliases
    override fun getDescription(): String? = parentDelegate.description
}

class EcoSubcommand(
    parentDelegate: CommandBase,
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean
) : HandledCommand(parentDelegate, plugin, name, permission, playersOnly)
