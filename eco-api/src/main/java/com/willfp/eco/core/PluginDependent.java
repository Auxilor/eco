package com.willfp.eco.core;

import org.jetbrains.annotations.NotNull;

/**
 * Quick DI class to manage passing eco plugins.
 *
 * @param <T> The eco plugin type.
 */
public abstract class PluginDependent<T extends EcoPlugin> {
    /**
     * The {@link EcoPlugin} that is stored.
     */
    @NotNull
    private final T plugin;

    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    protected PluginDependent(@NotNull final T plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    @NotNull
    protected T getPlugin() {
        return this.plugin;
    }
}
