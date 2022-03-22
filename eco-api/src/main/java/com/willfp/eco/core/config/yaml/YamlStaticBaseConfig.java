package com.willfp.eco.core.config.yaml;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.core.config.yaml.wrapper.LoadableYamlConfigWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Non-updatable yaml config that exists within a plugin jar.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@SuppressWarnings("removal")
@Deprecated(since = "6.17.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "6.30.0")
public abstract class YamlStaticBaseConfig extends LoadableYamlConfigWrapper {
    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected YamlStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final PluginLike plugin) {
        super(Eco.getHandler().getConfigFactory().createLoadableConfig(configName, plugin, "", plugin.getClass(), ConfigType.YAML));
    }

    /**
     * Config implementation for configs present in the plugin's base directory (eg config.yml, lang.yml).
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     */
    protected YamlStaticBaseConfig(@NotNull final String configName,
                                   @NotNull final EcoPlugin plugin) {
        this(configName, (PluginLike) plugin);
    }
}
