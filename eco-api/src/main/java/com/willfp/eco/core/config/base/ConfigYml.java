package com.willfp.eco.core.config.base;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.yaml.YamlBaseConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Default plugin config.yml.
 */
public class ConfigYml extends YamlBaseConfig {
    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin) {
        super("config", true, plugin);
    }

    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     * @param removeUnused Remove unused.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin, boolean removeUnused) {
        super("config", removeUnused, plugin);
    }

    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     * @param name The config name.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin, String name) {
        super(name, true, plugin);
    }

    /**
     * Config.yml.
     *
     * @param plugin The plugin.
     * @param name The config name.
     * @param removeUnused Remove unused.
     */
    public ConfigYml(@NotNull final EcoPlugin plugin, String name, boolean removeUnused) {
        super(name, removeUnused, plugin);
    }

}
