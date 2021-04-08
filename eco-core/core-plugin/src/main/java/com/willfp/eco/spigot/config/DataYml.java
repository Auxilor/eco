package com.willfp.eco.spigot.config;

import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import org.jetbrains.annotations.NotNull;

public class DataYml extends BaseConfig {
    /**
     * Init data.yml.
     *
     * @param plugin EcoSpigotPlugin.
     */
    public DataYml(@NotNull final EcoSpigotPlugin plugin) {
        super("data", false, plugin);
    }
}
