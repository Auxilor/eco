package com.willfp.eco.util.internal;

import com.willfp.eco.util.plugin.EcoPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class PluginDependent {
    /**
     * The {@link EcoPlugin} that is stored.
     */
    @Getter(AccessLevel.PROTECTED)
    private final EcoPlugin plugin;

    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    protected PluginDependent(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }
}
