package com.willfp.eco.internal.command

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.NotificationException
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

/**
 * Abstract class for commands that can be handled.
 * <p>
 * Handled commands have a method to pass in raw input from bukkit commands
 * in order to execute the command-specific code.
 */
abstract class EcoHandledCommand(
    private val parentDelegate: CommandBase,
    private val plugin: EcoPlugin,
    private val name: String,
    private val permission: String,
    private val playersOnly: Boolean
) : CommandBase, CommandExecutor, TabCompleter {

    /**
     * All subcommands for the command.
     */
    val subCommands = mutableListOf<CommandBase>()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (!command.name.equals(name, true)) {
            return false
        }

        if (args != null) {
            handleExecution(sender, args.toList())
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return handleTabComplete(sender, args?.toList() ?: listOf()).toMutableList()
    }

    override fun getPlugin() = this.plugin

    override fun getName() = this.name

    override fun getPermission() = this.permission

    override fun isPlayersOnly() = this.playersOnly

    override fun getSubcommands() = this.subCommands

    override fun getWrapped() = this.parentDelegate

    override fun addSubcommand(command: CommandBase): CommandBase {
        subCommands.add(command)
        return this
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) = parentDelegate.onExecute(sender, args)

    override fun onExecute(sender: Player, args: MutableList<String>) = parentDelegate.onExecute(sender, args)

    override fun tabComplete(sender: CommandSender, args: MutableList<String>): List<String> =
        parentDelegate.tabComplete(sender, args)

    override fun tabComplete(sender: Player, args: MutableList<String>): List<String> =
        parentDelegate.tabComplete(sender, args)

    fun handleExecutionBridge(sender: CommandSender, args: List<String>) {
        this.handleExecution(sender, args)
    }

    /**
     * Handle the command.
     *
     * @param sender The sender.
     * @param args   The arguments.
     */
    private fun CommandBase.handleExecution(sender: CommandSender, args: List<String>) {
        if (!canExecute(sender, this, plugin)) {
            return
        }

        if (args.isNotEmpty()) {
            for (subCommand in subcommands) {
                if (subCommand.name.equals(args[0], true)) {
                    if (!canExecute(sender, subCommand, plugin)) {
                        return
                    }

                    subCommand.handleExecution(sender, args.subList(1, args.size))
                    return
                }
            }
        }

        try {
            notifyFalse(!isPlayersOnly || sender is Player, "not-player")

            onExecute(sender, args)

            if (sender is Player) {
                onExecute(sender, args)
            }
        } catch (e: NotificationException) {
            sender.sendMessage(plugin.langYml.getMessage(e.key))
            return
        }
    }

    fun handleTabCompleteBridge(sender: CommandSender, args: List<String>): List<String> {
        return this.handleTabComplete(sender, args)
    }

    /**
     * Handle the tab completion.
     *
     * @param sender The sender.
     * @param args   The arguments.
     * @return The tab completion results.
     */
    private fun CommandBase.handleTabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (!sender.hasPermission(permission)) return emptyList()

        if (args.size == 1) {
            val completions = subcommands.filter { sender.hasPermission(it.permission) }.map { it.name }
            val list = mutableListOf<String>()
            StringUtil.copyPartialMatches(args[0], completions, list)
            if (completions.isNotEmpty()) {
                return completions
            }
        }

        if (args.size >= 2) {
            val matchingCommand =
                subcommands.firstOrNull {
                    sender.hasPermission(it.permission) && it.name.equals(args[0], true)
                }
            if (matchingCommand != null) {
                val completions =
                    matchingCommand.handleTabComplete(sender, args.subList(1, args.size)).toMutableList()
                if (sender is Player) {
                    completions.addAll(matchingCommand.handleTabComplete(sender, args.subList(1, args.size)))
                }

                return completions
            }
        }

        val completions = tabComplete(sender, args).toMutableList()
        if (sender is Player) {
            completions.addAll(tabComplete(sender, args))
        }
        return completions.sorted()
    }

    companion object {
        /**
         * Get the internal server CommandMap.
         *
         * @return The CommandMap.
         */
        fun getCommandMap(): CommandMap {
            try {
                val field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
                field.trySetAccessible()
                return field.get(Bukkit.getServer()) as CommandMap
            } catch (e: Exception) {
                throw NullPointerException("Command map wasn't found!")
            }
        }

        /**
         * If a sender can execute the command.
         *
         * @param sender  The sender.
         * @param command The command.
         * @param plugin  The plugin.
         * @return If the sender can execute.
         */
        fun canExecute(sender: CommandSender, command: CommandBase, plugin: EcoPlugin): Boolean {
            if (!sender.hasPermission(command.permission) && sender is Player) {
                sender.sendMessage(plugin.langYml.noPermission)
                return false
            }

            return true
        }
    }
}