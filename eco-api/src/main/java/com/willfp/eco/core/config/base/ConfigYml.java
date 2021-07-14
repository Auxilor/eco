package com.willfp.eco.core.config.base;

import com.willfp.eco.core.config.yaml.YamlBaseConfig;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;


public class ConfigYml extends YamlBaseConfig {
    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin) {
        super("config", true, plugin);
    }
}
