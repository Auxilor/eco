package com.willfp.eco.core;

import org.jetbrains.annotations.NotNull;

/**
 * Quick DI class to manage passing eco plugins.
 * <p>
 * Basically just a quick bit of laziness if you can't be bothered to add a private field
 * and a protected getter, don't use this in kotlin as you can just specify
 * {@code
 * private val plugin: EcoPlugin
 * }
 * in the constructor.
 *
 * @param <T> The eco plugin type.
 * @deprecated Leaky inheritance, shouldn't exist.
 */
@Deprecated(since = "6.43.0", forRemoval = true)
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
