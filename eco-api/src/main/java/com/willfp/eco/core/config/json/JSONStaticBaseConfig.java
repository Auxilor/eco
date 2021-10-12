package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.json.wrapper.LoadableJSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Non-updatable JSON config that exists within a plugin jar.
 */
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
        super(Eco.getHandler().getConfigFactory().createLoadableJSONConfig(configName, plugin, "", plugin.getClass()));
    }
}
