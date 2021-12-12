package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.json.wrapper.LoadableJSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Non-updatable JSON config that exists within a plugin jar.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated
public abstract class JSONStaticBaseConfig extends LoadableJSONConfigWrapper {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.json, lang.json).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected JSONStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final PluginLike plugin) {
        super((JSONConfig) Eco.getHandler().getConfigFactory().createLoadableConfig(configName, plugin, "", plugin.getClass(), ConfigType.JSON));
    }

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.json, lang.json).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected JSONStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final EcoPlugin plugin) {
        this(configName, (PluginLike) plugin);
    }
}
