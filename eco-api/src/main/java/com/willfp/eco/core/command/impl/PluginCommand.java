package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.PluginCommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * PluginCommands are the class to be used instead of CommandExecutor, they function as the base
 * command, e.g. {@code /ecoenchants} would be a base command, with each subsequent argument
 * functioning as subcommands.
 * <p>
 * The command will not be registered until register() is called.
 * <p>
 * The name cannot be the same as an existing command as this will conflict.
 */
public abstract class PluginCommand implements PluginCommandBase {
    /**
     * The delegate command.
     */
    private final PluginCommandBase delegate;

    /**
     * Create a new command.
     *
     * @param plugin      The plugin.
     * @param name        The name used in execution.
     * @param permission  The permission required to execute the command.
     * @param playersOnly If only players should be able to execute this command.
     */
    protected PluginCommand(@NotNull final EcoPlugin plugin,
                            @NotNull final String name,
                            @NotNull final String permission,
                            final boolean playersOnly) {
        this.delegate = Eco.get().createPluginCommand(this, plugin, name, permission, playersOnly);
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
    public void register() {
        delegate.register();
    }

    @Override
    public void unregister() {
        delegate.unregister();
    }

    @Override
    public EcoPlugin getPlugin() {
        return delegate.getPlugin();
    }
}
