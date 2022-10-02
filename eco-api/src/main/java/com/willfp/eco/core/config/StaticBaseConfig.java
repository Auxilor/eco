package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.wrapper.LoadableConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Non-updatable yaml config that exists within a plugin jar.
 */
public abstract class StaticBaseConfig extends LoadableConfigWrapper {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     * @param type       The config type.
     */
    protected StaticBaseConfig(@NotNull final String configName,
                               @NotNull final PluginLike plugin,
                               @NotNull final ConfigType type) {
        super(Eco.get().createLoadableConfig(
                configName,
                plugin,
                "",
                plugin.getClass(),
                type,
                true
        ));
    }
}
