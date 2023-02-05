package com.willfp.eco.internal.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.command.TabCompleter

class DelegatedBukkitCommand(
    private val delegate: EcoPluginCommand
) : Command(delegate.name), TabCompleter, PluginIdentifiableCommand {
    private var _aliases: List<String>? = null
    private var _description: String? = null

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
    override fun getDescription() = _description ?: delegate.description ?: ""
    override fun getAliases(): List<String> = _aliases ?: delegate.aliases

    override fun setDescription(description: String): Command {
        this._description = description
        return this
    }

    override fun setAliases(aliases: List<String>): Command {
        this._aliases = aliases
        return this
    }
}
