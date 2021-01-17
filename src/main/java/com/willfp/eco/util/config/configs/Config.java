package com.willfp.eco.util.config.configs;

import com.willfp.eco.util.config.BaseConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;


public class Config extends BaseConfig {
    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public Config(@NotNull final AbstractEcoPlugin plugin) {
        super("config", true, plugin);
    }
}
