package com.willfp.eco.core.config.yaml;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.yaml.wrapper.LoadableYamlConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
 * <p>
 * Automatically updates.
 */
public abstract class YamlBaseConfig extends LoadableYamlConfigWrapper {
    /**
     * @param configName      The name of the config
     * @param removeUnused    Whether keys not present in the default config should be removed on update.
     * @param plugin          The plugin.
     * @param updateBlacklist Substring of keys to not add/remove keys for.
     */
    protected YamlBaseConfig(@NotNull final String configName,
                             final boolean removeUnused,
                             @NotNull final PluginLike plugin,
                             @NotNull final String... updateBlacklist) {
        super(
                Eco.getHandler().getConfigFactory().createUpdatableYamlConfig(
                        configName,
                        plugin,
                        "",
                        plugin.getClass(),
                        removeUnused, updateBlacklist
                )
        );
    }

    /**
     * @param configName   The name of the config
     * @param removeUnused Whether keys not present in the default config should be removed on update.
     * @param plugin       The plugin.
     */
    protected YamlBaseConfig(@NotNull final String configName,
                             final boolean removeUnused,
                             @NotNull final PluginLike plugin) {
        super(
                Eco.getHandler().getConfigFactory().createUpdatableYamlConfig(
                        configName,
                        plugin,
                        "",
                        plugin.getClass(),
                        removeUnused
                )
        );
    }

    /**
     * @param configName      The name of the config
     * @param removeUnused    Whether keys not present in the default config should be removed on update.
     * @param plugin          The plugin.
     * @param updateBlacklist Substring of keys to not add/remove keys for.
     */
    protected YamlBaseConfig(@NotNull final String configName,
                             final boolean removeUnused,
                             @NotNull final EcoPlugin plugin,
                             @NotNull final String... updateBlacklist) {
        this(configName, removeUnused, (PluginLike) plugin, updateBlacklist);
    }

    /**
     * @param configName   The name of the config
     * @param removeUnused Whether keys not present in the default config should be removed on update.
     * @param plugin       The plugin.
     */
    protected YamlBaseConfig(@NotNull final String configName,
                             final boolean removeUnused,
                             @NotNull final EcoPlugin plugin) {
        this(configName, removeUnused, (PluginLike) plugin);
    }
}
