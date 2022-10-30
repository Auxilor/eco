package com.willfp.eco.core.command.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Delegates a bukkit command to an eco command (for registrations).
 */
public final class DelegatedBukkitCommand extends Command implements TabCompleter, PluginIdentifiableCommand {
    /**
     * The delegate command.
     */
    private final PluginCommand delegate;

    /**
     * Create a new delegated command.
     *
     * @param delegate The delegate.
     */
    public DelegatedBukkitCommand(@NotNull final PluginCommand delegate) {
        super(delegate.getName());

        this.delegate = delegate;
    }

    @Override
    public boolean execute(@NotNull final CommandSender commandSender,
                           @NotNull final String label,
                           @NotNull final String[] args) {
        return delegate.onCommand(commandSender, this, label, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender commandSender,
                                      @NotNull final Command command,
                                      @NotNull final String label,
                                      @NotNull final String[] args) {
        return delegate.onTabComplete(commandSender, command, label, args);
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return this.delegate.getPlugin();
    }

    @Nullable
    @Override
    public String getPermission() {
        return this.delegate.getPermission();
    }

    @NotNull
    @Override
    public String getDescription() {
        return this.delegate.getDescription() == null ? "" : this.delegate.getDescription();
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return this.delegate.getAliases();
    }
}
