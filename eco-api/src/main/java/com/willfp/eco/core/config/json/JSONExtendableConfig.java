package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.json.wrapper.LoadableJSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Config implementation for configs present in one of two places:
 * <ul>
 *     <li>Plugin base directory (eg config.yml, lang.yml)</li>
 *     <li>Other extension's configs</li>
 * </ul>
 * <p>
 * Automatically updates.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated(forRemoval = true)
public abstract class JSONExtendableConfig extends LoadableJSONConfigWrapper {
    /**
     * @param configName       The name of the config
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param plugin           The plugin.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     */
    protected JSONExtendableConfig(@NotNull final String configName,
                                   final boolean removeUnused,
                                   @NotNull final PluginLike plugin,
                                   @NotNull final Class<?> source,
                                   @NotNull final String subDirectoryPath,
                                   @NotNull final String... updateBlacklist) {
        super(
                (JSONConfig)
                        Eco.getHandler().getConfigFactory().createUpdatableConfig(
                                configName,
                                plugin,
                                subDirectoryPath,
                                source,
                                removeUnused,
                                ConfigType.JSON
                        )
        );
    }

    /**
     * @param configName       The name of the config
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param plugin           The plugin.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     */
    protected JSONExtendableConfig(@NotNull final String configName,
                                   final boolean removeUnused,
                                   @NotNull final EcoPlugin plugin,
                                   @NotNull final Class<?> source,
                                   @NotNull final String subDirectoryPath,
                                   @NotNull final String... updateBlacklist) {
        this(configName, removeUnused, (PluginLike) plugin, source, subDirectoryPath, updateBlacklist);
    }
}
