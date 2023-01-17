@file:JvmName("CommandHelperExtensions")

package com.willfp.eco.core.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.NotificationException
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Predicate

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
 * Kotlin builder for commands. Inherits plugin, permission, players
 * only.
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

/**
 * Throws an exception containing a langYml key if obj is null.
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param key key of notification message in langYml
 * @return Returns the object given or throws an exception
 * @throws NotificationException exception thrown when null
 */
fun <T> T.notifyNull(key: String): T {
    return this ?: throw NotificationException(key)
}

/**
 * Throws an exception containing a langYml key if predicate tests false
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param predicate predicate to test
 * @param key       key of notification message in langYml
 * @param <T>       the generic type of object
 * @return Returns the object given or throws an exception
 */
fun <T> T.notifyFalse(predicate: Predicate<T>, key: String): T {
    predicate.test(this).notifyFalse(key)
    return this
}

/**
 * Throws an exception containing a langYml key if condition is false.
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param key       value in the langYml
 * @return Returns the condition given or throws an exception
 * @throws NotificationException exception thrown when false
 */
fun Boolean?.notifyFalse(key: String): Boolean {
    return if (this == true) true else throw NotificationException(key)
}

/**
 * Throws an exception containing a langYml key if Bukkit.getPlayer(playerName) is null.
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param key        value in the langYml
 * @return Returns the player
 * @throws NotificationException exception thrown when invalid playerName
 */
fun String?.notifyPlayerRequired(key: String): Player {
    return Bukkit.getPlayer(this ?: "") ?: throw NotificationException(key)
}

/**
 * Throws an exception containing a langYml key if Bukkit.getPlayer(playerName) is null.
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param key        value in the langYml
 * @return Returns the player
 * @throws NotificationException exception thrown when invalid playerName
 */
fun String?.notifyOfflinePlayerRequired(key: String): OfflinePlayer {
    @Suppress("DEPRECATION")
    val player = Bukkit.getOfflinePlayer(this ?: "")

    if (!player.hasPlayedBefore() && !player.isOnline) {
        throw NotificationException(key)
    }

    return player
}

/**
 * Throws an exception containing a langYml key if player doesn't have permission.
 * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and Subcommand
 * automatically handles sending the message to the sender.</p>
 * <br>
 * @param permission the permission
 * @param key        value in the langYml
 * @return Returns the player
 * @throws NotificationException exception thrown when player doesn't have permission
 */
fun Player?.notifyPermissionRequired(permission: String, key: String): Player {
    this ?: throw NotificationException(key)
    return this.notifyFalse({ this.hasPermission(permission) }, key)
}
