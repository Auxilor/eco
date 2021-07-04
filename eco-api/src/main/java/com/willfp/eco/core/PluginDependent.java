package com.willfp.eco.core;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class PluginDependent<T extends EcoPlugin> {
    /**
     * The {@link EcoPlugin} that is stored.
     */
    @Getter(AccessLevel.PROTECTED)
    private final T plugin;

    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    protected PluginDependent(@NotNull final T plugin) {
        this.plugin = plugin;
    }
}
