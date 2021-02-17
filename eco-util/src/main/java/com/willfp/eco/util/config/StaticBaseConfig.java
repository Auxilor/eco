package com.willfp.eco.util.config;

import com.willfp.eco.internal.config.AbstractConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class StaticBaseConfig extends AbstractConfig {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected StaticBaseConfig(@NotNull final String configName,
                               @NotNull final AbstractEcoPlugin plugin) {
        super(configName, plugin, "", plugin.getClass());
    }
}
