package com.willfp.eco.core.config.configs;

import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;


public class Config extends BaseConfig {
    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public Config(@NotNull final EcoPlugin plugin) {
        super("config", true, plugin);
    }
}
