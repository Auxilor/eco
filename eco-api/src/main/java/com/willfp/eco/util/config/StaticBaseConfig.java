package com.willfp.eco.util.config;

import com.willfp.eco.internal.config.LoadableYamlConfig;
import com.willfp.eco.util.plugin.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class StaticBaseConfig extends LoadableYamlConfig {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected StaticBaseConfig(@NotNull final String configName,
                               @NotNull final EcoPlugin plugin) {
        super(configName, plugin, "", plugin.getClass());
    }
}
