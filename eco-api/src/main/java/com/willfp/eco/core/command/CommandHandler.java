package com.willfp.eco.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A command handler handles the actual code for a command.
 * <p>
 * The replacement for {@link org.bukkit.command.CommandExecutor#onCommand(CommandSender, Command, String, String[])}
 *
 * @see CommandBase
 * @deprecated Handlers have been deprecated. This legacy system will eventually be removed,
 * update to use the new system: {@link CommandBase#onExecute(CommandSender, List)}.
 */
@FunctionalInterface
@Deprecated(since = "6.17.0")
public interface CommandHandler {
    /**
     * The code to be called on execution.
     *
     * @param sender The sender.
     * @param args   The arguments.
     */
    void onExecute(@NotNull CommandSender sender,
                   @NotNull List<String> args);
}
