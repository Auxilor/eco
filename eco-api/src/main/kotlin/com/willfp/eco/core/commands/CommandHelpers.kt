@file:JvmName("CommandHelperExtensions")

package com.willfp.eco.core.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.core.command.NotificationException
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.floodgate.FloodgateService
import java.util.function.Predicate
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
    /**
     * Create a subcommand inheriting the plugin, permission and players only flag from a parent.
     *
     * @param parent The parent command.
     * @param name The command name.
     * @param executor The command executor.
     * @param tabCompleter The tab completer.
     */
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
 * @return The command.
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
 * Kotlin builder for subcommands, added to the receiver [CommandBase].
 *
 * @param name The command name.
 * @param permission The permission.
 * @param playersOnly If only players should execute the command.
 * @param init The builder.
 * @return The subcommand.
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
 * Kotlin builder for subcommands, added to the receiver [CommandBase]. Inherits plugin,
 * permission, players only.
 *
 * @param name The command name.
 * @param init The builder.
 * @return The subcommand.
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
 * Throw a [NotificationException] containing a lang.yml key if the receiver is null.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param key The key of the notification message in lang.yml.
 * @return The receiver, if it is not null.
 * @throws NotificationException If the receiver is null.
 */
fun <T> T.notifyNull(key: String): T {
    return this ?: throw NotificationException(key)
}

/**
 * Throw a [NotificationException] containing a lang.yml key if the predicate tests false
 * against the receiver.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param predicate The predicate to test the receiver against.
 * @param key       The key of the notification message in lang.yml.
 * @return The receiver, if the predicate passed.
 * @throws NotificationException If the predicate tests false.
 */
fun <T> T.notifyFalse(predicate: Predicate<T>, key: String): T {
    predicate.test(this).notifyFalse(key)
    return this
}

/**
 * Throw a [NotificationException] containing a lang.yml key if the receiver is not true,
 * i.e. if it is false or null.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param key The key of the notification message in lang.yml.
 * @return True, if the receiver was true.
 * @throws NotificationException If the receiver is false or null.
 */
fun Boolean?.notifyFalse(key: String): Boolean {
    return if (this == true) true else throw NotificationException(key)
}

/**
 * Look up an online [Player] by the receiver player name, throwing a [NotificationException]
 * containing a lang.yml key if there is no such player online.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param key The key of the notification message in lang.yml.
 * @return The player.
 * @throws NotificationException If the receiver is null or no player with that name is online.
 */
fun String?.notifyPlayerRequired(key: String): Player {
    return FloodgateService.findOnlinePlayer(this ?: "") ?: throw NotificationException(key)
}

/**
 * Look up an [OfflinePlayer] by the receiver player name, throwing a [NotificationException]
 * containing a lang.yml key if the player has never played before and is not online.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param key The key of the notification message in lang.yml.
 * @return The offline player.
 * @throws NotificationException If no player with that name has ever played on the server.
 */
fun String?.notifyOfflinePlayerRequired(key: String): OfflinePlayer {
    val player = FloodgateService.getOfflinePlayer(this ?: "")

    if (!player.hasPlayedBefore() && !player.isOnline) {
        throw NotificationException(key)
    }

    return player
}

/**
 * Throw a [NotificationException] containing a lang.yml key if the receiver player is null or
 * doesn't have the permission.
 *
 * [PluginCommand] and [Subcommand] automatically catch this in their execution and send the
 * message to the sender.
 *
 * @param permission The permission.
 * @param key        The key of the notification message in lang.yml.
 * @return The player.
 * @throws NotificationException If the receiver is null or doesn't have the permission.
 */
fun Player?.notifyPermissionRequired(permission: String, key: String): Player {
    this ?: throw NotificationException(key)
    return this.notifyFalse({ this.hasPermission(permission) }, key)
}
