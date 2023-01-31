package com.willfp.eco.internal.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.TabCompleter

class DelegatedBukkitCommand(
    private val delegate: EcoPluginCommand
) : Command(delegate.name), TabCompleter, PluginIdentifiableCommand {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        return delegate.onCommand(sender, this, label, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String> {
        return delegate.onTabComplete(sender, this, label, args) ?: emptyList()
    }

    override fun getPlugin() = delegate.plugin
    override fun getPermission() = delegate.permission
    override fun getDescription() = delegate.description ?: ""
    override fun getAliases(): List<String> = delegate.aliases
}
