package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.command.CommandBase;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.TabCompleteHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract class for commands that can be handled.
 * <p>
 * Handled commands have a method to pass in raw input from bukkit commands
 * in order to execute the command-specific code. It's essentially an internal
 * layer, hence why it's a package-private class.
 */
abstract class HandledCommand extends PluginDependent<EcoPlugin> implements CommandBase {
    /**
     * The name of the command.
     */
    private final String name;

    /**
     * The permission required to execute the command.
     * <p>
     * Written out as a string for flexibility with subclasses.
     */
    private final String permission;

    /**
     * Should the command only be allowed to be executed by players?
     * <p>
     * In other worlds, only allowed to be executed by console.
     */
    private final boolean playersOnly;

    /**
     * The actual code to be executed in the command.
     */
    private CommandHandler handler = (sender, args) -> {
    };

    /**
     * The tab completion code to be executed in the command.
     */
    private TabCompleteHandler tabCompleter = (sender, args) -> new ArrayList<>();

    /**
     * All subcommands for the command.
     */
    private final List<CommandBase> subcommands;

    /**
     * Create a new command.
     * <p>
     * The name cannot be the same as an existing command as this will conflict.
     *
     * @param plugin      Instance of a plugin.
     * @param name        The name used in execution.
     * @param permission  The permission required to execute the command.
     * @param playersOnly If only players should be able to execute this command.
     */
    HandledCommand(@NotNull final EcoPlugin plugin,
                   @NotNull final String name,
                   @NotNull final String permission,
                   final boolean playersOnly) {
        super(plugin);
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
        if (!canExecute(sender, this, this.getPlugin())) {
            return;
        }

        if (args.length > 0) {
            for (CommandBase subcommand : this.getSubcommands()) {
                if (subcommand.getName().equalsIgnoreCase(args[0])) {
                    if (!canExecute(sender, subcommand, this.getPlugin())) {
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
     * @return The tab completion results.
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

    /**
     * If a sender can execute the command.
     *
     * @param sender  The sender.
     * @param command The command.
     * @param plugin  The plugin.
     * @return If the sender can execute.
     */
    public static boolean canExecute(@NotNull final CommandSender sender,
                                     @NotNull final CommandBase command,
                                     @NotNull final EcoPlugin plugin) {
        if (command.isPlayersOnly() && !(sender instanceof Player)) {
            sender.sendMessage(plugin.getLangYml().getMessage("not-player"));
            return false;
        }

        if (!sender.hasPermission(command.getPermission()) && sender instanceof Player) {
            sender.sendMessage(plugin.getLangYml().getNoPermission());
            return false;
        }

        return true;
    }

    /**
     * Get the command name.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the permission required to execute the command.
     *
     * @return The permission.
     */
    public String getPermission() {
        return this.permission;
    }

    /**
     * Get if the command can only be executed by players.
     *
     * @return If players only.
     */
    public boolean isPlayersOnly() {
        return this.playersOnly;
    }

    /**
     * Get the actual code to be executed in the command.
     *
     * @return The code.
     */
    public CommandHandler getHandler() {
        return this.handler;
    }

    /**
     * Get the tab completion code to be executed in the command.
     *
     * @return The code.
     */
    public TabCompleteHandler getTabCompleter() {
        return this.tabCompleter;
    }

    /**
     * Get the subcommands of the command.
     *
     * @return The subcommands.
     */
    public List<CommandBase> getSubcommands() {
        return this.subcommands;
    }

    /**
     * Set the command handler.
     *
     * @param handler The handler.
     */
    public void setHandler(@NotNull final CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Set the tab completer.
     *
     * @param tabCompleter The tab completer.
     */
    public void setTabCompleter(@NotNull final TabCompleteHandler tabCompleter) {
        this.tabCompleter = tabCompleter;
    }
}
