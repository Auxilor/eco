package com.willfp.eco.spigot.config;

import com.willfp.eco.core.config.json.JSONStaticBaseConfig;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import org.jetbrains.annotations.NotNull;

public class DataJson extends JSONStaticBaseConfig {
    /**
     * Init data.json.
     *
     * @param plugin EcoSpigotPlugin.
     */
    public DataJson(@NotNull final EcoSpigotPlugin plugin) {
        super("data", plugin);
    }
}
