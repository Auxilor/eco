package com.willfp.eco.core.config.base;

import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;


public class ConfigYml extends BaseConfig {
    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin) {
        super("config", true, plugin);
    }
}
