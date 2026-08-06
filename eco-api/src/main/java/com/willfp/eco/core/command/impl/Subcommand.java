package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A command implementation that must exist as a subcommand (i.e. cannot be registered directly).
 */
public abstract class Subcommand implements CommandBase {
    /**
     * The delegate command.
     */
    private final CommandBase delegate;

    /**
     * Create subcommand.
     * <p>
     * The subcommand still has to be attached to a parent with
     * {@link CommandBase#addSubcommand(CommandBase)}.
     *
     * @param plugin      The plugin.
     * @param name        The subcommand name.
     * @param permission  The permission required to execute the subcommand.
     * @param playersOnly If only players should be able to execute this subcommand.
     */
    protected Subcommand(@NotNull final EcoPlugin plugin,
                         @NotNull final String name,
                         @NotNull final String permission,
                         final boolean playersOnly) {
        this.delegate = Eco.get().createSubcommand(this, plugin, name, permission, playersOnly);
    }

    /**
     * Create subcommand, inheriting the permission and players-only state of a parent command.
     * <p>
     * This does not attach the subcommand to the parent; use
     * {@link CommandBase#addSubcommand(CommandBase)} for that.
     *
     * @param plugin The plugin.
     * @param name   The name of the subcommand.
     * @param parent The command to inherit the permission and players-only state from.
     */
    protected Subcommand(@NotNull final EcoPlugin plugin,
                         @NotNull final String name,
                         @NotNull final CommandBase parent) {
        this(plugin, name, parent.getPermission(), parent.isPlayersOnly());
    }

    @Override
    public @NotNull String getName() {
        return delegate.getName();
    }

    @Override
    public @NotNull String getPermission() {
        return delegate.getPermission();
    }

    @Override
    public boolean isPlayersOnly() {
        return delegate.isPlayersOnly();
    }

    @Override
    public @NotNull CommandBase addSubcommand(@NotNull CommandBase command) {
        return delegate.addSubcommand(command);
    }

    @Override
    public @NotNull List<CommandBase> getSubcommands() {
        return delegate.getSubcommands();
    }

    @Override
    public @NotNull CommandBase getWrapped() {
        return this;
    }

    @Override
    public EcoPlugin getPlugin() {
        return delegate.getPlugin();
    }
}
