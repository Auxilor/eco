package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.wrapper.LoadableYamlConfigWrapper;
import com.willfp.eco.core.config.wrapper.WrappedBukkitConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class BaseConfig extends LoadableYamlConfigWrapper {

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
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Automatically updates.
     *
     * @param configName   The name of the config
     * @param removeUnused Whether keys not present in the default config should be removed on update.
     * @param plugin       The plugin.
     */
    protected BaseConfig(@NotNull final String configName,
                         final boolean removeUnused,
                         @NotNull final EcoPlugin plugin) {
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

    @Override
    public YamlConfiguration getBukkitHandle() {
        return (YamlConfiguration) ((WrappedBukkitConfig<?>) this.getHandle()).getBukkitHandle();
    }
}
