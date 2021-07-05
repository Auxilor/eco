package com.willfp.eco.internal.commands;

import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.TabCompleteHandler;
import com.willfp.eco.core.command.util.CommandUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HandledCommand implements CommandBase {
    /**
     * The name of the command.
     */
    @Getter
    private final String name;

    /**
     * The permission required to execute the command.
     * <p>
     * Written out as a string for flexibility with subclasses.
     */
    @Getter
    private final String permission;

    /**
     * Should the command only be allowed to be executed by players?
     * <p>
     * In other worlds, only allowed to be executed by console.
     */
    @Getter
    private final boolean playersOnly;

    /**
     * All subcommands for the command.
     */
    @Getter(AccessLevel.PROTECTED)
    private final List<CommandBase> subcommands;

    /**
     * Create a new command.
     * <p>
     * The name cannot be the same as an existing command as this will conflict.
     *
     * @param name        The name used in execution.
     * @param permission  The permission required to execute the command.
     * @param playersOnly If only players should be able to execute this command.
     */
    protected HandledCommand(@NotNull final String name,
                             @NotNull final String permission,
                             final boolean playersOnly) {
        this.name = name;
        this.permission = permission;
        this.playersOnly = playersOnly;
        this.subcommands = new ArrayList<>();
    }

    /**
     * Add a subcommand to the command.
     *
     * @param subcommand The subcommand.
     * @return The parent command.
     */
    @Override
    public final CommandBase addSubcommand(@NotNull final CommandBase subcommand) {
        subcommands.add(subcommand);

        return this;
    }

    /**
     * Handle the command.
     *
     * @param sender The sender.
     * @param args   The arguments.
     */
    protected final void handle(@NotNull final CommandSender sender,
                                @NotNull final String[] args) {
        if (!CommandUtils.canExecute(sender, this)) {
            return;
        }

        if (args.length > 0) {
            for (CommandBase subcommand : this.getSubcommands()) {
                if (subcommand.getName().equalsIgnoreCase(args[0])) {
                    if (!CommandUtils.canExecute(sender, subcommand)) {
                        return;
                    }

                    ((HandledCommand) subcommand).handle(sender, Arrays.copyOfRange(args, 1, args.length));

                    return;
                }
            }
        }

        this.getHandler().onExecute(sender, Arrays.asList(args));
    }

    /**
     * Handle the tab completion.
     *
     * @param sender The sender.
     * @param args   The arguments.
     */
    protected final List<String> handleTabCompletion(@NotNull final CommandSender sender,
                                                     @NotNull final String[] args) {

        if (!sender.hasPermission(this.getPermission())) {
            return null;
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            StringUtil.copyPartialMatches(
                    args[0],
                    this.getSubcommands().stream().map(CommandBase::getName).collect(Collectors.toList()),
                    completions
            );

            Collections.sort(completions);

            if (!completions.isEmpty()) {
                return completions;
            }
        }

        if (args.length >= 2) {
            HandledCommand command = null;

            for (CommandBase subcommand : this.getSubcommands()) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    command = (HandledCommand) subcommand;
                }
            }

            if (command != null) {
                return command.handleTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return this.getTabCompleter().tabComplete(sender, Arrays.asList(args));
    }

    @Override
    public abstract CommandHandler getHandler();

    @Override
    public TabCompleteHandler getTabCompleter() {
        return (sender, args) -> new ArrayList<>();
    }
}
