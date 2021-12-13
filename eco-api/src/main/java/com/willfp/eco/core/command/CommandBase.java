package com.willfp.eco.core.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for all command implementations.
 */
public interface CommandBase {
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
     * <p>
     * Marked as default void with no implementation for backwards compatibility.
     *
     * @param sender The sender.
     * @param args   The args.
     */
    default void onExecute(@NotNull CommandSender sender,
                           @NotNull List<String> args) {
        // Do nothing.
    }

    /**
     * Handle tab completion.
     * <p>
     * Marked as default void with no implementation for backwards compatibility.
     *
     * @param sender The sender.
     * @param args   The args.
     * @return The results.
     */
    default List<String> tabComplete(@NotNull CommandSender sender,
                                     @NotNull List<String> args) {
        return new ArrayList<>();
    }

    /**
     * Get the handler.
     *
     * @return The handler.
     * @see CommandHandler
     * @deprecated Use {@link CommandBase#onExecute(CommandSender, List)} instead.
     */
    @Deprecated
    CommandHandler getHandler();

    /**
     * Set the handler.
     *
     * @param handler The handler.
     * @see CommandHandler
     * @deprecated Handlers have been deprecated.
     */
    @Deprecated
    void setHandler(@NotNull CommandHandler handler);

    /**
     * Get the tab completer.
     *
     * @return The tab completer.
     * @see TabCompleteHandler
     * @deprecated Use {@link CommandBase#tabComplete(CommandSender, List)} instead.
     */
    @Deprecated
    TabCompleteHandler getTabCompleter();

    /**
     * Set the tab completer.
     *
     * @param handler The handler.
     * @see TabCompleteHandler
     * @deprecated Handlers have been deprecated.
     */
    @Deprecated
    void setTabCompleter(@NotNull TabCompleteHandler handler);
}
