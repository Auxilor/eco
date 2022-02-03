@file:JvmName("CommandHelperExtensions")

package com.willfp.eco.core.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.command.CommandSender

/**
 * Helper class for creating commands with builders.
 *
 * @param plugin The plugin.
 * @param name The command name.
 * @param permission The permission.
 * @param playersOnly If only players should run the command.
 * @param executor The command executor.
 * @param tabCompleter The tab completer.
 */
class BuiltPluginCommand internal constructor(
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean = false,
    var executor: (CommandSender, List<String>) -> Unit,
    var tabCompleter: (CommandSender, List<String>) -> List<String>,
) : PluginCommand(plugin, name, permission, playersOnly) {
    override fun onExecute(sender: CommandSender, args: List<String>) =
        executor(sender, args)

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> =
        tabCompleter(sender, args)
}


/**
 * Helper class for creating commands with builders.
 *
 * @param plugin The plugin.
 * @param name The command name.
 * @param permission The permission.
 * @param playersOnly If only players should run the command.
 * @param executor The command executor.
 * @param tabCompleter The tab completer.
 */
class BuiltSubcommand internal constructor(
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean = false,
    var executor: (CommandSender, List<String>) -> Unit,
    var tabCompleter: (CommandSender, List<String>) -> List<String>,
) : Subcommand(plugin, name, permission, playersOnly) {
    internal constructor(
        parent: CommandBase,
        name: String,
        executor: (CommandSender, List<String>) -> Unit,
        tabCompleter: (CommandSender, List<String>) -> List<String>,
    ) : this(parent.plugin, name, parent.permission, parent.isPlayersOnly, executor, tabCompleter)

    override fun onExecute(sender: CommandSender, args: List<String>) =
        executor(sender, args)

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> =
        tabCompleter(sender, args)
}

/**
 * Kotlin builder for commands.
 *
 * @param plugin The plugin.
 * @param name The command name.
 * @param permission The permission.
 * @param playersOnly If only players should execute the command.
 * @param init The builder.
 */
fun command(
    plugin: EcoPlugin,
    name: String,
    permission: String,
    playersOnly: Boolean = false,
    init: BuiltPluginCommand.() -> Unit
): PluginCommand {
    val command = BuiltPluginCommand(
        plugin,
        name,
        permission,
        playersOnly,
        { _, _ -> },
        { _, _ -> emptyList() }
    )
    init(command)
    return command
}

/**
 * Kotlin builder for commands.
 *
 * @param name The command name.
 * @param permission The permission.
 * @param playersOnly If only players should execute the command.
 * @param init The builder.
 */
fun CommandBase.addSubcommand(
    name: String,
    permission: String,
    playersOnly: Boolean = false,
    init: BuiltSubcommand.() -> Unit
): Subcommand {
    val command = BuiltSubcommand(
        this.plugin,
        name,
        permission,
        playersOnly,
        { _, _ -> },
        { _, _ -> emptyList() }
    )
    init(command)
    return command
}

/**
 * Kotlin builder for commands.
 * Inherits plugin, permission, players only.
 *
 * @param name The command name.
 * @param init The builder.
 */
fun CommandBase.addSubcommand(
    name: String,
    init: BuiltSubcommand.() -> Unit
): Subcommand {
    val command = BuiltSubcommand(
        this,
        name,
        { _, _ -> },
        { _, _ -> emptyList() }
    )
    init(command)
    return command
}
