package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * PluginCommands are the class to be used instead of CommandExecutor.
 * <p>
 * The command will not be registered until register() is called.
 * <p>
 * The name cannot be the same as an existing command as this will conflict.
 */
public abstract class PluginCommand extends HandledCommand implements CommandExecutor, TabCompleter {
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
        super(plugin, name, permission, playersOnly);
    }

    /**
     * Registers the command with the server,
     * <p>
     * Requires the command name to exist, defined in plugin.yml.
     */
    public final void register() {
        org.bukkit.command.PluginCommand command = Bukkit.getPluginCommand(this.getName());
        assert command != null;
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    /**
     * Internal implementation used to clean up boilerplate.
     * Used for parity with {@link CommandExecutor#onCommand(CommandSender, Command, String, String[])}.
     *
     * @param sender  The executor of the command.
     * @param command The bukkit command.
     * @param label   The name of the executed command.
     * @param args    The arguments of the command (anything after the physical command name)
     * @return If the command was processed by the linked {@link EcoPlugin}
     */
    @Override
    public final boolean onCommand(@NotNull final CommandSender sender,
                                   @NotNull final Command command,
                                   @NotNull final String label,
                                   @NotNull final String[] args) {
        if (!command.getName().equalsIgnoreCase(this.getName())) {
            return false;
        }

        this.handle(sender, args);

        return true;
    }

    /**
     * Internal implementation used to clean up boilerplate.
     * Used for parity with {@link TabCompleter#onTabComplete(CommandSender, Command, String, String[])}.
     *
     * @param sender  The executor of the command.
     * @param command The bukkit command.
     * @param label   The name of the executed command.
     * @param args    The arguments of the command (anything after the physical command name).
     * @return The list of tab-completions.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender,
                                                @NotNull final Command command,
                                                @NotNull final String label,
                                                @NotNull final String[] args) {
        if (!command.getName().equalsIgnoreCase(this.getName())) {
            return null;
        }

        return this.handleTabCompletion(sender, args);
    }
}
