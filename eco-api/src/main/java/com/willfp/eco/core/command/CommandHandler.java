package com.willfp.eco.core.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
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
