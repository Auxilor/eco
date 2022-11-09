package com.willfp.eco.core.command.impl;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@SuppressWarnings({"DeprecatedIsStillUsed", "removal"})
abstract class HandledCommand implements CommandBase {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

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
    @Deprecated
    @Nullable
    private com.willfp.eco.core.command.CommandHandler handler = null;

    /**
     * The tab completion code to be executed in the command.
     */
    @Deprecated
    @Nullable
    private com.willfp.eco.core.command.TabCompleteHandler tabCompleter = null;

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
        this.plugin = plugin;
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
     * Get the plugin.
     *
     * @return The plugin.
     */
    @Override
    public EcoPlugin getPlugin() {
        return this.plugin;
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

        if (this.isPlayersOnly() && !(sender instanceof Player)) {
            sender.sendMessage(this.getPlugin().getLangYml().getMessage("not-player"));
            return;
        }

        if (this.getHandler() != null) {
            this.getHandler().onExecute(sender, Arrays.asList(args));
        } else {
            this.onExecute(sender, Arrays.asList(args));
            if (sender instanceof Player player) {
                this.onExecute(player, Arrays.asList(args));
            }
        }
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
                    this.getSubcommands().stream()
                            .filter(subCommand -> sender.hasPermission(subCommand.getPermission()))
                            .map(CommandBase::getName)
                            .collect(Collectors.toList()),
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
                if (!sender.hasPermission(subcommand.getPermission())) {
                    continue;
                }

                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    command = (HandledCommand) subcommand;
                }
            }

            if (command != null) {
                return command.handleTabCompletion(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        if (this.getTabCompleter() != null) {
            return this.getTabCompleter().tabComplete(sender, Arrays.asList(args));
        } else {
            List<String> completions = new ArrayList<>(this.tabComplete(sender, Arrays.asList(args)));
            if (sender instanceof Player player) {
                completions.addAll(this.tabComplete(player, Arrays.asList(args)));
            }
            return completions;
        }
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
     * Get the subcommands of the command.
     *
     * @return The subcommands.
     */
    public List<CommandBase> getSubcommands() {
        return this.subcommands;
    }

    @Deprecated(forRemoval = true)
    @Override
    public @Nullable com.willfp.eco.core.command.CommandHandler getHandler() {
        return this.handler;
    }

    @Deprecated(forRemoval = true)
    @Override
    public @Nullable com.willfp.eco.core.command.TabCompleteHandler getTabCompleter() {
        return this.tabCompleter;
    }

    @Deprecated(forRemoval = true)
    @Override
    public void setHandler(@Nullable final com.willfp.eco.core.command.CommandHandler handler) {
        this.handler = handler;
    }

    @Deprecated(forRemoval = true)
    @Override
    public void setTabCompleter(@Nullable final com.willfp.eco.core.command.TabCompleteHandler tabCompleter) {
        this.tabCompleter = tabCompleter;
    }
}
