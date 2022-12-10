package com.willfp.eco.core.command;

import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Interface for all command implementations.
 */
@SuppressWarnings({"removal", "null"})
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

    @NotNull List<CommandBase> getSubcommands();

    @NotNull CommandBase getWrapped();

    /**
     * Handle command execution.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull CommandSender sender,
                           @NotNull List<String> args) throws NotificationException {
        // Do nothing.
        Bukkit.getLogger().info("Did this happen?");
    }

    /**
     * Handle command execution from players.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull Player sender,
                           @NotNull List<String> args) throws NotificationException {
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
    default List<String> tabComplete(@NotNull CommandSender sender,
                                     @NotNull List<String> args) {
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
    default List<String> tabComplete(@NotNull Player sender,
                                     @NotNull List<String> args) {
        return new ArrayList<>();
    }

    /**
     * Throws an exception containing a langYml key if obj is null.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase derivative:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param obj the object
     * @param key key of notification message in langYml
     * @param <T> the generic type of object
     * @return Returns the object given or throws an exception
     * @throws NotificationException exception thrown when null
     */
    default @NotNull <T> T notifyNull(@Nullable T obj, @NotNull String key)
            throws NotificationException {
        if (Objects.isNull(obj)) {
            throw new NotificationException(key);
        }

        return obj;
    }

    /**
     * Throws an exception containing a langYml key if predicate tests false
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase derivative:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param obj       Object to test with predicate
     * @param predicate predicate to test
     * @param key       key of notification message in langYml
     * @param <T>       the generic type of object
     * @return Returns the object given or throws an exception
     */
    default @NotNull <T> T notifyFalse(@NotNull T obj,
                                       @NotNull Predicate<T> predicate, @NotNull String key)
            throws NotificationException {
        notifyFalse(predicate.test(obj), key);
        return obj;
    }

    /**
     * Throws an exception containing a langYml key if condition is false.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase derivative:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param condition the condition, throws exception if false
     * @param key       value in the langYml
     * @return Returns the condition given or throws an exception
     * @throws NotificationException exception thrown when false
     */
    default boolean notifyFalse(boolean condition, @NotNull String key)
            throws NotificationException {
        if (!condition) {
            throw new NotificationException(key);
        }
        return true;
    }

    /**
     * Throws an exception containing a langYml key if Bukkit.getPlayer(playerName) is null.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase derivative:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param playerName the player name
     * @param key        value in the langYml
     * @return Returns the player
     * @throws NotificationException exception thrown when invalid playerName
     */
    default @NotNull Player notifyPlayerRequired(@NotNull String playerName,
                                                 @NotNull String key)
            throws NotificationException {

        final Player player = Bukkit.getPlayer(playerName);

        notifyNull(player, key);

        return Objects.requireNonNull(player);
    }

    /**
     * Throws an exception containing a langYml key if player doesn't have permission.
     * <p>The {@link CommandBase#onExecute(CommandSender, List) onExecute } in PluginCommand and SubCommand
     * automatically handles sending the message to the sender.</p>
     * <br>
     * Works with any CommandBase derivative:
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>PluginCommand#{@link com.willfp.eco.core.command.impl.PluginCommand#onExecute(Player, List) onExecute(Player, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(CommandSender, List) onExecute(CommandSender, List)}</p>
     * <p>Subcommand#{@link com.willfp.eco.core.command.impl.Subcommand#onExecute(Player, List) onExecute(Player, List)}</p>
     *
     * @param player     the player
     * @param permission the permission
     * @param key        value in the langYml
     * @return Returns the player
     * @throws NotificationException exception thrown when player doesn't have permission
     */
    default @NotNull Player notifyPermissionRequired(@NotNull Player player, @NotNull String permission, @NotNull String key) throws NotificationException {
        return notifyFalse(player, (p -> p.hasPermission(permission)), key);
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    EcoPlugin getPlugin();

    /**
     * Get the handler.
     *
     * @return The handler.
     * @see CommandHandler
     * @deprecated Use {@link CommandBase#onExecute(CommandSender, List)} instead.
     */
    @Deprecated(forRemoval = true)
    default CommandHandler getHandler() {
        return (a, b) -> {

        };
    }

    /**
     * Set the handler.
     *
     * @param handler The handler.
     * @see CommandHandler
     * @deprecated Handlers have been deprecated.
     */
    @Deprecated(forRemoval = true)
    default void setHandler(@NotNull final CommandHandler handler) {
        // Do nothing.
    }

    /**
     * Get the tab completer.
     *
     * @return The tab completer.
     * @see TabCompleteHandler
     * @deprecated Use {@link CommandBase#tabComplete(CommandSender, List)} instead.
     */
    @Deprecated(forRemoval = true)
    default TabCompleteHandler getTabCompleter() {
        return (a, b) -> ImmutableList.of();
    }

    /**
     * Set the tab completer.
     *
     * @param handler The handler.
     * @see TabCompleteHandler
     * @deprecated Handlers have been deprecated.
     */
    @Deprecated(forRemoval = true)
    default void setTabCompleter(@NotNull final TabCompleteHandler handler) {
        // Do nothing.
    }
}
