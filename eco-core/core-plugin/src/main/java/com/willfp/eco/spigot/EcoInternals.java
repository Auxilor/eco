package com.willfp.eco.spigot;

import com.willfp.eco.internal.Internals;
import org.jetbrains.annotations.NotNull;

public class EcoInternals extends Internals {
    private final EcoSpigotPlugin plugin;

    EcoInternals(@NotNull final EcoSpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public EcoSpigotPlugin getPlugin() {
        return plugin;
    }
}
