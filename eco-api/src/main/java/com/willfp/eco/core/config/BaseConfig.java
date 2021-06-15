package com.willfp.eco.core.config;

import com.willfp.eco.internal.config.yaml.UpdatableYamlConfig;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class BaseConfig extends UpdatableYamlConfig {

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Automatically updates.
     *
     * @param configName      The name of the config
     * @param removeUnused    Whether keys not present in the default config should be removed on update.
     * @param plugin          The plugin.
     * @param updateBlacklist Substring of keys to not add/remove keys for.
     */
    protected BaseConfig(@NotNull final String configName,
                         final boolean removeUnused,
                         @NotNull final EcoPlugin plugin,
                         @NotNull final String... updateBlacklist) {
        super(configName, plugin, "", plugin.getClass(), removeUnused, updateBlacklist);
    }

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Automatically updates.
     *
     * @param configName      The name of the config
     * @param removeUnused    Whether keys not present in the default config should be removed on update.
     * @param plugin          The plugin.
     */
    protected BaseConfig(@NotNull final String configName,
                         final boolean removeUnused,
                         @NotNull final EcoPlugin plugin) {
        super(configName, plugin, "", plugin.getClass(), removeUnused, "");
    }
}
