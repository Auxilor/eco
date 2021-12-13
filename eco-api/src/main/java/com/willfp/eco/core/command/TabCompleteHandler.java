package com.willfp.eco.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A Tab Complete handler handles the actual tab-completion code.
 * <p>
 * The replacement for {@link org.bukkit.command.TabCompleter#onTabComplete(CommandSender, Command, String, String[])}
 *
 * @see CommandBase
 * @deprecated Handlers have been deprecated. This legacy system will eventually be removed,
 * update to use the new system.
 */
@FunctionalInterface
@Deprecated(since = "6.17.0")
public interface TabCompleteHandler {
    /**
     * Handle Tab Completion.
     *
     * @param sender The sender.
     * @param args   The arguments.
     * @return The tab completion results.
     */
    List<String> tabComplete(@NotNull CommandSender sender,
                             @NotNull List<String> args);
}
