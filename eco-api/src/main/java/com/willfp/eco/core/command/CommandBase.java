package com.willfp.eco.core.command;

import org.jetbrains.annotations.NotNull;

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
     * Get the handler.
     *
     * @return The handler.
     */
    CommandHandler getHandler();

    /**
     * Get the tab completer.
     *
     * @return The tab completer.
     */
    TabCompleteHandler getTabCompleter();
}
