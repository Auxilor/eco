package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.TabCompleteHandler;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class HandledCommand implements CommandBase, CommandExecutor, TabCompleter {
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

    @Override
    public abstract CommandHandler getHandler();

    @Override
    public TabCompleteHandler getTabCompleter() {
        return (sender, args) -> new ArrayList<>();
    }
}
