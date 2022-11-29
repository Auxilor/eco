package com.willfp.eco.core.command;

import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.EcoPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for all command implementations.
 */
@SuppressWarnings("removal")
public interface CommandBase {

    /**
     * Get aliases. Leave null if this command is from plugin.yml.
     *
     * @return The aliases.
     */
    @NotNull
    default List<String> getAliases() {
        return new ArrayList<>();
    }

    /**
     * Get description.
     *
     * @return The description.
     */
    @Nullable
    default public String getDescription() {
        return null;
    }

    /**
     * Get command name.
     *
     * @return The name.
     */
    String getName();

    /**
     * Get command permission.
     *
     * @return The permission.
     */
    String getPermission();

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
    CommandBase addSubcommand(@NotNull CommandBase command);

    /**
     * Handle command execution.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull CommandSender sender,
        @NotNull List<String> args) throws ArgumentAssertionException {
        // Do nothing.
    }

    /**
     * Handle command execution from players.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull Player sender,
        @NotNull List<String> args) throws ArgumentAssertionException {
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

    void register();

    void unregister();

    /**
     * Throws an exception and sends a lang message if obj null
     *
     * @param obj        the object
     * @param langTarget value in the langYml
     * @param <T>        the generic type of object
     * @return Returns the object given or throws an exception
     * @throws ArgumentAssertionException exception thrown when null
     */
    default @NotNull <T> Optional<T> assertNonNull(@Nullable T obj, @NotNull String langTarget)
        throws ArgumentAssertionException {
        return Optional.empty();
    }

    /**
     * Throws an exception if predicate tests false
     *
     * @param obj        Object to test with predicate
     * @param predicate  predicate to test
     * @param langTarget value in the langYml
     * @param <T>        the generic type of object
     * @return Returns the object given or throws an exception
     * @throws ArgumentAssertionException
     */
    default @NotNull <T> Optional<T> assertPredicate(@Nullable T obj,
        @NotNull Predicate<T> predicate, @NotNull String langTarget)
        throws ArgumentAssertionException {
        return Optional.empty();
    }

    /**
     * Throws an exception and sends a lang message if Bukkit.getPlayer(player) is null
     *
     * @param player     the player name
     * @param langTarget value in the langYml
     * @return Returns the player
     * @throws ArgumentAssertionException exception thrown when invalid player
     */
    default @NotNull Optional<Player> assertPlayer(@NotNull String player,
        @NotNull String langTarget)
        throws ArgumentAssertionException {
        return Optional.empty();
    }


    /**
     * @param condition  the condition, throws exception if false
     * @param langTarget value in the langYml
     * @return Returns the condition given or throws an exception
     * @throws ArgumentAssertionException exception thrown when false
     */
    default boolean assertCondition(boolean condition, @NotNull String langTarget)
        throws ArgumentAssertionException {
        return true;
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
