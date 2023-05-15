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
     * The use local storage key.
     */
    public static final String KEY_USES_LOCAL_STORAGE = "use-local-storage";

    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin) {
        super("config", plugin, true, ConfigType.YAML);
    }

    /**
     * Config.yml.
     *
     * @param plugin       The plugin.
     * @param removeUnused Remove unused.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     final boolean removeUnused) {
        super("config", plugin, removeUnused, ConfigType.YAML);
    }

    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     * @param name   The config name.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     @NotNull final String name) {
        super(name, plugin, true, ConfigType.YAML);
    }

    /**
     * Config.yml.
     *
     * @param plugin       The plugin.
     * @param name         The config name.
     * @param removeUnused Remove unused.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin,
                     @NotNull final String name,
                     final boolean removeUnused) {
        super(name, plugin, removeUnused, ConfigType.YAML);
    }

    /**
     * Get if the plugin is using local storage.
     *
     * @return The prefix.
     */
    public boolean isUsingLocalStorage() {
        return this.getBool(KEY_USES_LOCAL_STORAGE);
    }
}
