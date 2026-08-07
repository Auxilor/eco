package com.willfp.eco.core.command;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Plugin command bases can be registered directly with the server,
 * this essentially functions as the interface that is implemented generically
 * via {@link com.willfp.eco.core.command.impl.PluginCommand}.
 */
public interface PluginCommandBase extends CommandBase {
    /**
     * Register the PluginCommandBase to the bukkit commandMap.
     */
    void register();

    /**
     * Unregister the PluginCommandBase from the bukkit commandMap.
     */
    void unregister();

    /**
     * Get the aliases for the command.
     * <p>
     * These are only used if the command isn't declared in plugin.yml; if it is, the aliases
     * from plugin.yml take priority.
     *
     * @return The aliases, empty by default.
     */
    @NotNull
    default List<String> getAliases() {
        return new ArrayList<>();
    }

    /**
     * Get the description of the command.
     * <p>
     * This is only used if the command isn't declared in plugin.yml; if it is, the description
     * from plugin.yml takes priority.
     *
     * @return The description, or null if the command has no description.
     */
    @Nullable
    default String getDescription() {
        return null;
    }
}
