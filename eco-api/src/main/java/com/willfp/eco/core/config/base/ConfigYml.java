package com.willfp.eco.core.config.base;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.config.ConfigType;
import org.jetbrains.annotations.NotNull;

/**
 * Default plugin config.yml.
 */
public class ConfigYml extends BaseConfig {
    /**
     * The key for whether the plugin should use local storage.
     */
    public static final String KEY_USES_LOCAL_STORAGE = "use-local-storage";

    /**
     * Create a new config.yml, removing unused keys on update.
     *
     * @param plugin The plugin.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin) {
        super("config", plugin, true, ConfigType.YAML);
    }

    /**
     * Create a new config.yml.
     *
     * @param plugin       The plugin.
     * @param removeUnused If unused sections should be removed on update.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     final boolean removeUnused) {
        super("config", plugin, removeUnused, ConfigType.YAML);
    }

    /**
     * Create a new yaml base config with a custom name, removing unused keys on update.
     *
     * @param plugin The plugin.
     * @param name   The config name (excluding extension).
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     @NotNull final String name) {
        super(name, plugin, true, ConfigType.YAML);
    }

    /**
     * Create a new yaml base config with a custom name.
     *
     * @param plugin       The plugin.
     * @param name         The config name (excluding extension).
     * @param removeUnused If unused sections should be removed on update.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     @NotNull final String name,
                     final boolean removeUnused) {
        super(name, plugin, removeUnused, ConfigType.YAML);
    }

    /**
     * Get if the plugin is using local storage.
     *
     * @return If the plugin is using local storage.
     */
    public boolean isUsingLocalStorage() {
        return this.getBool(KEY_USES_LOCAL_STORAGE);
    }
}
