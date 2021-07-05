package com.willfp.eco.core.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
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
