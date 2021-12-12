package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.PluginLike;
import org.jetbrains.annotations.NotNull;

/**
 * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
 * <p>
 * Automatically updates.
 */
public abstract class BaseConfig extends LoadableConfigWrapper {
    /**
     * Create new Base Config.
     *
     * @param plugin       The plugin or extension.
     * @param configName   The config name (excluding extension).
     * @param removeUnused If unused sections should be removed.
     * @param type         The config type.
     */
    protected BaseConfig(@NotNull final String configName,
                         @NotNull final PluginLike plugin,
                         final boolean removeUnused,
                         @NotNull final ConfigType type) {
        super(Eco.getHandler().getConfigFactory().createUpdatableConfig(
                configName,
                plugin,
                "",
                plugin.getClass(),
                removeUnused,
                type
        ));
    }
}
