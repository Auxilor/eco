package com.willfp.eco.core.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
     * Get aliases. Leave null if this command is from plugin.yml.
     *
     * @return The aliases.
     */
    @NotNull
    default List<String> getAliases() {
        return new ArrayList<>();
    }

    /**
     * Get description.
     *
     * @return The description.
     */
    @Nullable
    default String getDescription() {
        return null;
    }
}
