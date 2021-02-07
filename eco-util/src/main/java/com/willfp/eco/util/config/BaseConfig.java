package com.willfp.eco.util.config;

import com.willfp.eco.util.config.internal.AbstractUpdatableConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class BaseConfig extends AbstractUpdatableConfig {

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
                         @NotNull final AbstractEcoPlugin plugin,
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
                         @NotNull final AbstractEcoPlugin plugin) {
        super(configName, plugin, "", plugin.getClass(), removeUnused, "");
    }
}
