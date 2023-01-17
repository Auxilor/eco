package com.willfp.eco.core.command;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Generic interface for commands.
 */
@SuppressWarnings("null")
public interface CommandBase {

    /**
     * Get command name.
     *
     * @return The name.
     */
    @NotNull String getName();

    /**
     * Get command permission.
     *
     * @return The permission.
     */
    @NotNull String getPermission();

    /**
     * If only players can execute the command.
     *
     * @return If true.
     */
    boolean isPlayersOnly();

    /**
     * Add a subcommand to the command.
     *
     * @param command The subcommand.
     * @return The parent command.
     */
    @NotNull CommandBase addSubcommand(@NotNull CommandBase command);

    /**
     * Get the subcommands of the command.
     *
     * @return The subcommands.
     */
    @NotNull List<CommandBase> getSubcommands();

    /**
     * Intended for returning the enclosing CommandBase,
     * when this instance is serving as the delegate command base.
     *
     * @return the wrapping object of this delegate.
     */
    default @NotNull CommandBase getWrapped() {
        return this;
    }

    /**
     * Handle command execution.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull final CommandSender sender, @NotNull final List<String> args) {
        // Do nothing.
    }

    /**
     * Handle command execution from players.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull final Player sender, @NotNull final List<String> args) {
        // Do nothing.
    }

    /**
     * Handle tab completion.
     *
     * @param sender The sender.
     * @param args   The args.
     * @return The results.
     */
    @NotNull
    default List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final List<String> args) {
        return new ArrayList<>();
    }

    /**
     * Handle tab completion.
     *
     * @param sender The sender.
     * @param args   The args.
     * @return The results.
     */
    @NotNull
    default List<String> tabComplete(@NotNull final Player sender, @NotNull final List<String> args) {
        return new ArrayList<>();
    }

    /**
     * Throws an exception containing a langYml key.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * ]
     *
     * @param key The lang.yml key for the message to be sent.
     * @throws NotificationException always.
     */
    default void notify(@NotNull final String key) throws NotificationException {
        throw new NotificationException(key);
    }

    /**
     * Throws an exception containing a langYml key if obj is null.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param obj The object to test.
     * @param key The lang.yml key for the message to be sent.
     * @param <T> The object type.
     * @return Returns a definitely not-null object or throws an exception.
     * @throws NotificationException If the passed object is null.
     */
    default @NotNull <T> T notifyNull(@Nullable final T obj, @NotNull final String key) throws NotificationException {
        if (Objects.isNull(obj)) {
            notify(key);
        }

        return Objects.requireNonNull(obj);
    }

    /**
     * Throws an exception containing a langYml key if predicate tests false
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param obj       The object to test.
     * @param predicate The predicate on the object.
     * @param key       The lang.yml key for the message to be sent.
     * @param <T>       The object type.
     * @return Returns the object given or throws an exception.
     * @throws NotificationException If the predicate is false.
     */
    default @NotNull <T> T notifyFalse(@NotNull final T obj, @NotNull final String key, @NotNull final Predicate<T> predicate) throws NotificationException {
        notifyFalse(predicate.test(obj), key);

        return obj;
    }

    /**
     * Throws an exception containing a langYml key if condition is false.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param condition The condition to be met.
     * @param key       The lang.yml key for the message to be sent.
     * @return Returns the condition given or throws an exception.
     * @throws NotificationException If the condition is false.
     */
    default boolean notifyFalse(final boolean condition, @NotNull final String key) throws NotificationException {
        if (!condition) {
            notify(key);
        }

        return true;
    }

    /**
     * Throws an exception containing a langYml key if Bukkit.getPlayer(playerName) is null.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param playerName The player name.
     * @param key        The lang.yml key for the message to be sent.
     * @return Returns the player.
     * @throws NotificationException If given an invalid player name.
     */
    default @NotNull Player notifyPlayerRequired(@NotNull final String playerName, @NotNull final String key) throws NotificationException {
        final Player player = Bukkit.getPlayer(playerName);

        notifyNull(player, key);

        return Objects.requireNonNull(player);
    }

    /**
     * Throws an exception containing a langYml key if Bukkit.getPlayer(playerName) is null.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param playerName The player name.
     * @param key        The lang.yml key for the message to be sent.
     * @return Returns the player.
     * @throws NotificationException If given an invalid player name.
     */
    default @NotNull OfflinePlayer notifyOfflinePlayerRequired(@NotNull final String playerName, @NotNull final String key) throws NotificationException {
        @SuppressWarnings("deprecation") final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

        boolean hasPlayedBefore = player.hasPlayedBefore() || player.isOnline();

        notifyFalse(!hasPlayedBefore, key);

        return player;
    }

    /**
     * Throws an exception containing a langYml key if player doesn't have permission.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase implementation:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param player     The player.
     * @param permission The permission.
     * @param key        The lang.yml key for the message to be sent.
     * @return The player.
     * @throws NotificationException If the player doesn't have the required permission.
     */
    default @NotNull Player notifyPermissionRequired(@NotNull final Player player, @NotNull final String permission, @NotNull final String key) throws NotificationException {
        return notifyFalse(player, key, p -> p.hasPermission(permission));
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    EcoPlugin getPlugin();
}
