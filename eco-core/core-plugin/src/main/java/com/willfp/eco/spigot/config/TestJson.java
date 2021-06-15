package com.willfp.eco.spigot.config;

import com.willfp.eco.core.config.JsonStaticBaseConfig;
import com.willfp.eco.spigot.EcoSpigotPlugin;
import org.jetbrains.annotations.NotNull;

public class TestJson extends JsonStaticBaseConfig {
    /**
     * Init data.yml.
     *
     * @param plugin EcoSpigotPlugin.
     */
    public TestJson(@NotNull final EcoSpigotPlugin plugin) {
        super("test", plugin);
    }
}
