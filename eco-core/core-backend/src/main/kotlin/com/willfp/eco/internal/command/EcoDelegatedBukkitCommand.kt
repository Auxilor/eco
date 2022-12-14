package com.willfp.eco.internal.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * Delegates a bukkit command to an eco command (for registrations).
 */
class EcoDelegatedBukkitCommand(private val delegate: EcoPluginCommand) : Command(delegate.name), TabCompleter,
    PluginIdentifiableCommand {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        val argsAsList = args?.toMutableList() ?: mutableListOf()
        delegate.onExecute(sender, argsAsList)
        if (sender is Player) {
            delegate.onExecute(sender, argsAsList)
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String> {
        val argsAsList = args?.toMutableList() ?: mutableListOf()
        return when (sender) {
            is Player -> {
                delegate.tabComplete(sender, argsAsList)
            }

            else -> delegate.tabComplete(sender, argsAsList)
        }.toMutableList()
    }

    override fun getPlugin() = delegate.plugin
    override fun getPermission() = delegate.permission
    override fun getDescription() = delegate.description ?: ""
    override fun getAliases(): MutableList<String> = delegate.aliases

}