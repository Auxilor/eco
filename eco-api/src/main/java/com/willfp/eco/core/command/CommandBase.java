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
     * <p>
     * This will always be called on command execution.
     *
     * @param sender The sender.
     * @param args   The args.
     * @throws NotificationException naturally, this is handled as a part of the command system.
     */
    default void onExecute(@NotNull final CommandSender sender, @NotNull final List<String> args) throws NotificationException {
        // Do nothing.
    }

    /**
     * Handle command execution from players.
     * <p>
     * This will only be called if the sender is a player.
     *
     * @param sender The sender.
     * @param args   The args.
     * @throws NotificationException naturally, this is handled as a part of the command system.
     */
    default void onExecute(@NotNull final Player sender, @NotNull final List<String> args) throws NotificationException {
        // Do nothing.
    }

    /**
     * Handle tab completion.
     * <p>
     * This will always be called on tab completion.
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
     * <p>
     * This will only be called if the sender is a player.
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
     * Throws an {@link NotificationException} relating to a specific lang.yml key.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param key The lang.yml key for the message to be sent.
     * @throws NotificationException always.
     */
    default void notify(@NotNull final String key) throws NotificationException {
        throw new NotificationException(key);
    }

    /**
     * Throws an {@link NotificationException} relating to a specific lang.yml key
     * if the passed object is null.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param obj The object to test.
     * @param key The lang.yml key for the message to be sent.
     * @param <T> The object type.
     * @return Returns the object, definitely not-null.
     * @throws NotificationException If the object is null.
     */
    @NotNull
    default <T> T notifyNull(@Nullable final T obj,
                             @NotNull final String key) throws NotificationException {
        if (Objects.isNull(obj)) {
            notify(key);
        }

        return Objects.requireNonNull(obj);
    }

    /**
     * Throws an {@link NotificationException} relating to a specific lang.yml key
     * if the passed object doesn't match the predicate.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param obj       The object to test.
     * @param key       The lang.yml key for the message to be sent.
     * @param predicate The predicate to test the object against.
     * @param <T>       The type of the object.
     * @return Returns the object, definitely not-null.
     * @throws NotificationException If the object doesn't satisfy the predicate.
     */
    @NotNull
    default <T> T notifyFalse(@NotNull final T obj,
                              @NotNull final String key,
                              @NotNull final Predicate<T> predicate) throws NotificationException {
        notifyFalse(predicate.test(obj), key);

        return obj;
    }


    /**
     * Throws an {@link NotificationException} relating to a specific lang.yml key
     * if a condition is false.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param condition The condition to test.
     * @param key       The lang.yml key for the message to be sent.
     * @return True.
     * @throws NotificationException If the condition is false.
     */
    default boolean notifyFalse(final boolean condition,
                                @NotNull final String key) throws NotificationException {
        if (!condition) {
            notify(key);
        }

        return true;
    }

    /**
     * Throws an {@link NotificationException} relating to a specific lang.yml key
     * if the passed string doesn't relate to a currently online player.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param playerName The player name.
     * @param key        The lang.yml key for the message to be sent.
     * @return Returns the player, definitely not-null.
     * @throws NotificationException If the player name is invalid.
     */
    @NotNull
    default Player notifyPlayerRequired(@Nullable final String playerName, @NotNull final String key) throws NotificationException {
        if (playerName == null) {
            notify(key);
        }

        assert playerName != null;

        final Player player = Bukkit.getPlayer(playerName);

        notifyNull(player, key);

        return Objects.requireNonNull(player);
    }

    /**
     * Throws an {@link NotificationException} relating to a specific lang.yml key
     * if the passed string doesn't relate to a player on the server.
     * <p>
     * This is automatically handled with eco, and should not be surrounded by a
     * try/catch block.
     *
     * @param playerName The player name.
     * @param key        The lang.yml key for the message to be sent.
     * @return Returns the offline player, definitely not-null.
     * @throws NotificationException If the player name is invalid.
     */
    @NotNull
    default OfflinePlayer notifyOfflinePlayerRequired(@Nullable final String playerName,
                                                      @NotNull final String key) throws NotificationException {
        if (playerName == null) {
            notify(key);
        }

        assert playerName != null;

        @SuppressWarnings("deprecation") final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

        boolean hasPlayedBefore = player.hasPlayedBefore() || player.isOnline();

        notifyFalse(!hasPlayedBefore, key);

        return player;
    }

    /**
     * Throws an exception containing a langYml key if player doesn't have permission.
     *
     * @param player     The player.
     * @param permission The permission.
     * @param key        The lang.yml key for the message to be sent.
     * @return The player.
     * @throws NotificationException If the player doesn't have the required permission.
     */
    @NotNull
    default Player notifyPermissionRequired(@NotNull final Player player,
                                            @NotNull final String permission,
                                            @NotNull final String key) throws NotificationException {
        return notifyFalse(player, key, p -> p.hasPermission(permission));
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    EcoPlugin getPlugin();
}
