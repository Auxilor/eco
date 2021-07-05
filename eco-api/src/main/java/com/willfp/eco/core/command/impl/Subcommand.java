package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.command.CommandBase;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand extends HandledCommand {
    /**
     * Create subcommand.
     *
     * @param name        The subcommand name.
     * @param permission  The subcommand permission.
     * @param playersOnly If the subcommand only works on players.
     */
    protected Subcommand(@NotNull final String name,
                         @NotNull final String permission,
                         final boolean playersOnly) {
        super(name, permission, playersOnly);
    }

    /**
     * Create subcommand.
     *
     * @param name   The name of the subcommand.
     * @param parent The parent command.
     */
    protected Subcommand(@NotNull final String name,
                         @NotNull final CommandBase parent) {
        super(name, parent.getPermission(), parent.isPlayersOnly());
    }
}
