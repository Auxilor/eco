package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCommand extends HandledCommand implements CommandExecutor, TabCompleter {
    /**
     * Create a new command.
     * <p>
     * The command will not be registered until {@link this#register()} is called.
     * <p>
     * The name cannot be the same as an existing command as this will conflict.
     * @param name        The name used in execution.
     * @param permission  The permission required to execute the command.
     * @param playersOnly If only players should be able to execute this command.
     */
    protected BaseCommand(@NotNull final String name,
                          @NotNull final String permission,
                          final boolean playersOnly) {
        super(name, permission, playersOnly);
    }

    /**
     * Registers the command with the server,
     * <p>
     * Requires the command name to exist, defined in plugin.yml.
     */
    public final void register() {
        PluginCommand command = Bukkit.getPluginCommand(this.getName());
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

        if (!CommandUtils.canExecute(sender, this)) {
            return true;
        }

        if (args.length > 0) {
            for (CommandBase subcommand : this.getSubcommands()) {
                if (subcommand.getName().equalsIgnoreCase(args[0])) {
                    if (!CommandUtils.canExecute(sender, subcommand)) {
                        return true;
                    }

                    subcommand.getHandler().onExecute(sender, Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));

                    return true;
                }
            }
        }

        this.getHandler().onExecute(sender, Arrays.asList(args));

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

        if (!sender.hasPermission(this.getPermission())) {
            return null;
        }

        if (args.length > 0) {
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

        return this.getTabCompleter().tabComplete(sender, Arrays.asList(args));
    }
}
