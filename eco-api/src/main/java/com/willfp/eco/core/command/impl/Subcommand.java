package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.internal.commands.HandledCommand;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand extends HandledCommand {
    /**
     * Create subcommand.
     *
     * @param plugin      The plugin.
     * @param name        The subcommand name.
     * @param permission  The subcommand permission.
     * @param playersOnly If the subcommand only works on players.
     */
    protected Subcommand(@NotNull final EcoPlugin plugin,
                         @NotNull final String name,
                         @NotNull final String permission,
                         final boolean playersOnly) {
        super(plugin, name, permission, playersOnly);
    }

    /**
     * Create subcommand.
     *
     * @param plugin      The plugin.
     * @param name   The name of the subcommand.
     * @param parent The parent command.
     */
    protected Subcommand(@NotNull final EcoPlugin plugin,
                         @NotNull final String name,
                         @NotNull final CommandBase parent) {
        super(plugin, name, parent.getPermission(), parent.isPlayersOnly());
    }
}
