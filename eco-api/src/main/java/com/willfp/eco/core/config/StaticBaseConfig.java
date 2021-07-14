package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.wrapper.LoadableYamlConfigWrapper;
import com.willfp.eco.core.config.wrapper.YamlConfigWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class StaticBaseConfig extends LoadableYamlConfigWrapper {
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
        super(Eco.getHandler().getConfigFactory().createLoadableYamlConfig(configName, plugin, "", plugin.getClass()));
    }
}
