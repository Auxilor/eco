package com.willfp.eco.core.config.yaml;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.yaml.wrapper.LoadableYamlConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Non-updatable yaml config that exists within a plugin jar.
 */
public abstract class YamlStaticBaseConfig extends LoadableYamlConfigWrapper {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected YamlStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final EcoPlugin plugin) {
        super(Eco.getHandler().getConfigFactory().createLoadableYamlConfig(configName, plugin, "", plugin.getClass()));
    }
}
