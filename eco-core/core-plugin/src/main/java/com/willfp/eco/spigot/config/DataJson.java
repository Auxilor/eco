package com.willfp.eco.spigot.config;

import com.willfp.eco.core.config.json.JsonStaticBaseConfig;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import org.jetbrains.annotations.NotNull;

public class DataJson extends JsonStaticBaseConfig {
    /**
     * Init data.json.
     *
     * @param plugin EcoSpigotPlugin.
     */
    public DataJson(@NotNull final EcoSpigotPlugin plugin) {
        super("data", plugin);
    }
}
