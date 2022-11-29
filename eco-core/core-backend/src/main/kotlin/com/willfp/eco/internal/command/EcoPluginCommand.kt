package com.willfp.eco.internal.command

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.ArgumentAssertionException
import com.willfp.eco.core.command.CommandBase
import org.bukkit.Bukkit
import org.bukkit.command.*
import org.bukkit.entity.Player

class EcoPluginCommand(
    _plugin: EcoPlugin,
    _name: String,
    _permission: String,
    _playersOnly: Boolean
) : CommandBase, CommandExecutor, TabCompleter {


    val ecoPlugin: EcoPlugin
    val commandName: String
    val commandPermission: String
    val playersOnly: Boolean

    init {
        ecoPlugin = _plugin
        commandName = _name
        commandPermission = _permission
        playersOnly = _playersOnly
    }

    val subcommands = mutableListOf<CommandBase>()

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

            // TODO MOVE DELEGATED BUKKIT COMMAND TO BACKEND
            // map.register(plugin.name.lowercase(), DelegatedBukkitCommand(this))

        }

        Eco.get().syncCommands()
    }

    override fun unregister() {
        val map = getCommandMap()
        val found = map.getCommand(name)
        found?.unregister(map)

        Eco.get().syncCommands()
    }

    override fun onCommand(
        commandSender: CommandSender,
        command: Command,
        s: String,
        strings: Array<out String>?
    ): Boolean {

        if (!command.name.equals(name, true)) {
            return false
        }

        if (strings != null) {
            handleExecution(commandSender, strings.toList())
        }

        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        s: String,
        strings: Array<out String>?
    ): MutableList<String>? {
        TODO("IMPLEMENT ON TAB COMPLETE")
    }

    override fun getPlugin(): EcoPlugin = ecoPlugin

    override fun getName(): String = commandName

    override fun getPermission(): String = commandPermission

    override fun isPlayersOnly(): Boolean = playersOnly

    override fun addSubcommand(command: CommandBase): CommandBase {
        TODO("Not yet implemented")
    }

    fun CommandBase.handleExecution(sender: CommandSender, args: List<String>) {
        if (!canExecute(sender, this, plugin)) {
            return
        }

        if (args.isNotEmpty()) {
            for (subCommand in subcommands) {
                if (subCommand.name.equals(args[0], true) && !canExecute(
                        sender,
                        subCommand,
                        plugin
                    )
                ) {
                    return
                }

                subCommand.handleExecution(sender, args.subList(1, args.size))
                return
            }
        }

        try {
            assertCondition(isPlayersOnly && sender !is Player, "not-player")

            if (sender is Player) {
                onExecute(sender, args)
            } else {
                onExecute(sender, args)
            }

        } catch (e: ArgumentAssertionException) {
            sender.sendMessage(plugin.langYml.getMessage(e.langTarget))
            return
        }
    }

    fun CommandBase.handleTabComplete(sender: CommandSender, args: List<String>): List<String> {
        TODO("Havent done this yet")
    }

    companion object {
        fun getCommandMap(): CommandMap {
            try {
                val field = Bukkit.getServer().javaClass.getDeclaredField("commandMap");
                field.trySetAccessible()
                return field.get(Bukkit.getServer()) as CommandMap
            } catch (e: Exception) {
                throw NullPointerException("Command map wasn't found!");
            }
        }

        fun canExecute(sender: CommandSender, command: CommandBase, plugin: EcoPlugin): Boolean {
            if (!sender.hasPermission(command.permission) && sender is Player) {
                sender.sendMessage(plugin.langYml.noPermission)
                return false
            }

            return true
        }
    }
}