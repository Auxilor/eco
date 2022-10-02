package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.wrapper.LoadableConfigWrapper;
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
        this(configName, plugin, removeUnused, type, true);
    }

    /**
     * Create new Base Config.
     *
     * @param plugin               The plugin or extension.
     * @param configName           The config name (excluding extension).
     * @param removeUnused         If unused sections should be removed.
     * @param type                 The config type.
     * @param requiresChangeToSave If changes must be applied to save the config.
     */
    protected BaseConfig(@NotNull final String configName,
                         @NotNull final PluginLike plugin,
                         final boolean removeUnused,
                         @NotNull final ConfigType type,
                         final boolean requiresChangeToSave) {
        super(Eco.get().createUpdatableConfig(
                configName,
                plugin,
                "",
                plugin.getClass(),
                removeUnused,
                type,
                requiresChangeToSave
        ));
    }
}
