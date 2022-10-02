package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.wrapper.LoadableConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Config implementation for configs present in one of two places:
 * <ul>
 *     <li>Plugin base directory (eg config.yml, lang.json)</li>
 *     <li>Other extension's configs</li>
 * </ul>
 * <p>
 * Automatically updates.
 */
public abstract class ExtendableConfig extends LoadableConfigWrapper {
    /**
     * @param configName       The name of the config
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param plugin           The plugin.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     * @param subDirectoryPath The subdirectory path.
     * @param type             The config type.
     * @param source           The class that owns the resource.
     */
    protected ExtendableConfig(@NotNull final String configName,
                               final boolean removeUnused,
                               @NotNull final PluginLike plugin,
                               @NotNull final Class<?> source,
                               @NotNull final String subDirectoryPath,
                               @NotNull final ConfigType type,
                               @NotNull final String... updateBlacklist) {
        super(Eco.get().createUpdatableConfig(
                configName,
                plugin,
                subDirectoryPath,
                source,
                removeUnused,
                type,
                true,
                updateBlacklist
        ));
    }
}
