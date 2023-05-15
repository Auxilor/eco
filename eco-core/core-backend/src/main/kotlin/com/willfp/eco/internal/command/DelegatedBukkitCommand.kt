package com.willfp.eco.internal.command

import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.TabCompleter

class DelegatedBukkitCommand(
    private val delegate: EcoPluginCommand
) : Command(
    delegate.name,
    delegate.description ?: "",
    "/${delegate.name}",
    delegate.aliases
), TabCompleter, PluginIdentifiableCommand {
    override fun execute(sender: CommandSender, label: String, args: Array<out String>?): Boolean {
        return delegate.onCommand(sender, this, label, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String> {
        return delegate.onTabComplete(sender, this, label, args)
    }

    override fun tabComplete(
        sender: CommandSender,
        alias: String,
        args: Array<out String>?,
        location: Location?
    ): List<String> {
        return delegate.onTabComplete(sender, this, alias, args)
    }

    override fun getPlugin() = delegate.plugin
    override fun getPermission() = delegate.permission
}
