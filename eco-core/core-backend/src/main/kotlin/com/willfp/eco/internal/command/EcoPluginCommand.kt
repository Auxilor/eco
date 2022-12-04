package com.willfp.eco.internal.command

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.NotificationException
import com.willfp.eco.core.command.RegistrableCommandBase
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class EcoPluginCommand(
    _plugin: EcoPlugin,
    _name: String,
    _permission: String,
    _playersOnly: Boolean
) : RegistrableCommandBase, CommandExecutor, TabCompleter {


    val ecoPlugin: EcoPlugin
    val commandName: String
    val commandPermission: String
    val playersOnly: Boolean
    val subCommands = mutableListOf<CommandBase>()

    init {
        ecoPlugin = _plugin
        commandName = _name
        commandPermission = _permission
        playersOnly = _playersOnly
    }

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

    override fun getSubcommands() = subCommands

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
            notifyFalse(isPlayersOnly && sender !is Player, "not-player")

            if (sender is Player) {
                onExecute(sender, args)
            } else {
                onExecute(sender, args)
            }

        } catch (e: NotificationException) {
            sender.sendMessage(plugin.langYml.getMessage(e.key))
            return
        }
    }

    /*
        protected final List<String> handleTabCompletion(@NotNull final CommandSender sender,
                                                     @NotNull final String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            return null;
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(
                    args[0],
                    this.getSubcommands().stream()
                            .filter(subCommand -> sender.hasPermission(subCommand.getPermission()))
                            .map(CommandBase::getName)
                            .collect(Collectors.toList()),
                    completions
            );

            Collections.sort(completions);

            if (!completions.isEmpty()) {
                return completions;
            }
        }

        if (args.length >= 2) {
            HandledCommand command = null;

            for (CommandBase subcommand : this.getSubcommands()) {
                if (!sender.hasPermission(subcommand.getPermission())) {
                    continue;
                }

                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    command = (HandledCommand) subcommand;
                }
            }

            if (command != null) {
                return command.handleTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        if (this.getTabCompleter() != null) {
            return this.getTabCompleter().tabComplete(sender, Arrays.asList(args));
        } else {
            List<String> completions = new ArrayList<>(this.tabComplete(sender, Arrays.asList(args)));
            if (sender instanceof Player player) {
                completions.addAll(this.tabComplete(player, Arrays.asList(args)));
            }
            return completions;
        }
    }
     */
    fun CommandBase.handleTabComplete(sender: CommandSender, args: List<String>): List<String> {
        if (!sender.hasPermission(permission) || args.isEmpty()) return emptyList()

        val completions = subCommands.filter { sender.hasPermission(it.permission) }.map { it.name }.sorted()

        return when (args.size) {
            1 -> {
                val list = mutableListOf<String>()
                StringUtil.copyPartialMatches(args[0], completions, list)
                list
            }

            else -> completions
        }
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